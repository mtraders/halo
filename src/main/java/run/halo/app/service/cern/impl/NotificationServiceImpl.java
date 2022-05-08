package run.halo.app.service.cern.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import run.halo.app.event.cern.NotificationUpdateEvent;
import run.halo.app.event.logger.LogEvent;
import run.halo.app.model.entity.Category;
import run.halo.app.model.entity.Content;
import run.halo.app.model.entity.PostCategory;
import run.halo.app.model.entity.PostTag;
import run.halo.app.model.entity.Tag;
import run.halo.app.model.entity.cern.Notification;
import run.halo.app.model.enums.LogType;
import run.halo.app.model.params.cern.CernPostQuery;
import run.halo.app.model.vo.cern.notification.NotificationDetailVO;
import run.halo.app.repository.cern.NotificationRepository;
import run.halo.app.service.CategoryService;
import run.halo.app.service.ContentPatchLogService;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.PostCategoryService;
import run.halo.app.service.PostTagService;
import run.halo.app.service.TagService;
import run.halo.app.service.assembler.cern.NotificationAssembler;
import run.halo.app.service.assembler.cern.CernAssembler;
import run.halo.app.service.cern.NotificationService;
import run.halo.app.service.impl.BasePostServiceImpl;
import run.halo.app.utils.ServiceUtils;

import java.util.List;
import java.util.Set;

/**
 * notification service impl.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Slf4j
@Service
public class NotificationServiceImpl extends BasePostServiceImpl<Notification> implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final ContentService contentService;
    private final ContentPatchLogService contentPatchLogService;
    private final OptionService optionService;
    private final CernAssembler<Notification> notificationCernAssembler;
    private final ApplicationContext applicationContext;
    private final ApplicationEventPublisher eventPublisher;
    private final TagService tagService;
    private final PostTagService postTagService;
    private final CategoryService categoryService;
    private final PostCategoryService postCategoryService;
    private final ContentService postContentService;
    private final ContentPatchLogService postContentPatchLogService;
    private final NotificationAssembler notificationAssembler;


    /**
     * constructor of notification service impl.
     *
     * @param notificationRepository notification repository.
     * @param optionService option service.
     * @param contentService content service.
     * @param contentPatchLogService content patch log service.
     * @param notificationCernAssembler notification query service.
     * @param applicationContext application context
     * @param eventPublisher event publisher.
     * @param tagService tag service.
     * @param postTagService post tag service.
     * @param categoryService category service.
     * @param postCategoryService post category service.
     * @param postContentService post content service.
     * @param postContentPatchLogService post content patch log service.
     * @param notificationAssembler notification assembler.
     */
    public NotificationServiceImpl(NotificationRepository notificationRepository, OptionService optionService, ContentService contentService,
                                   ContentPatchLogService contentPatchLogService, CernAssembler<Notification> notificationCernAssembler,
                                   ApplicationContext applicationContext, ApplicationEventPublisher eventPublisher,
                                   TagService tagService, PostTagService postTagService,
                                   CategoryService categoryService, PostCategoryService postCategoryService,
                                   ContentService postContentService, ContentPatchLogService postContentPatchLogService,
                                   NotificationAssembler notificationAssembler) {
        super(notificationRepository, optionService, contentService, contentPatchLogService);
        this.notificationRepository = notificationRepository;
        this.contentService = contentService;
        this.contentPatchLogService = contentPatchLogService;
        this.optionService = optionService;
        this.notificationCernAssembler = notificationCernAssembler;
        this.applicationContext = applicationContext;
        this.eventPublisher = eventPublisher;
        this.tagService = tagService;
        this.postTagService = postTagService;
        this.categoryService = categoryService;
        this.postCategoryService = postCategoryService;
        this.postContentService = postContentService;
        this.postContentPatchLogService = postContentPatchLogService;
        this.notificationAssembler = notificationAssembler;
    }

    /**
     * Get post with the latest content by id.
     * content from patch log.
     *
     * @param postId post id.
     * @return post with the latest content.
     */
    @Override
    public Notification getWithLatestContentById(Integer postId) {
        Notification notification = getById(postId);
        Content content = getContentById(postId);
        Content.PatchedContent patchedContent = contentPatchLogService.getPatchedContentById(content.getHeadPatchLogId());
        notification.setContent(patchedContent);
        return notification;
    }

    /**
     * pages notifications.
     *
     * @param notificationQuery query param.
     * @param pageable page info.
     * @return notification page.
     */
    @Override
    @NonNull
    public Page<Notification> pageBy(@NonNull CernPostQuery<Notification> notificationQuery, @NonNull Pageable pageable) {
        Assert.notNull(notificationQuery, "Notification query info must not be null");
        Assert.notNull(pageable, "Notification page info must not be null");
        Specification<Notification> notificationSpecification = notificationCernAssembler.buildSpecByQuery(notificationQuery, Notification.class);
        return notificationRepository.findAll(notificationSpecification, pageable);
    }

    /**
     * create a notification.
     *
     * @param notification notification entity.
     * @param tagIds tag ids.
     * @param categoryIds category ids.
     * @param autoSave auto-save or not
     * @return notification detail vo.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @NonNull
    public NotificationDetailVO createBy(@NonNull Notification notification, Set<Integer> tagIds, Set<Integer> categoryIds, boolean autoSave) {
        NotificationDetailVO notificationDetailVO = createOrUpdate(notification, tagIds, categoryIds);
        if (!autoSave) {
            LogEvent logEvent =
                new LogEvent(this, notificationDetailVO.getId().toString(), LogType.NOTIFICATION_PUBLISHED, notificationDetailVO.getTitle());
            eventPublisher.publishEvent(logEvent);
        }
        return notificationDetailVO;
    }

    /**
     * create or update notifitcation.
     *
     * @param notification notification entity.
     * @param tagIds tag ids.
     * @param categoryIds category ids.
     * @return notification detail vo.
     */
    private NotificationDetailVO createOrUpdate(@NonNull Notification notification, Set<Integer> tagIds, Set<Integer> categoryIds) {
        Assert.notNull(notification, "Notification must not be null");
        notification = super.createOrUpdateBy(notification);

        Integer id = notification.getId();
        // remove existed tags and categories
        postTagService.removeByPostId(id);
        postCategoryService.removeByPostId(id);

        List<Tag> tags = tagService.listAllByIds(tagIds);
        List<Category> categories = categoryService.listAllByIds(categoryIds);
        List<PostTag> notificationTags = postTagService.mergeOrCreateByIfAbsent(id, ServiceUtils.fetchProperty(tags, Tag::getId));
        log.debug("Created notification tags: [{}]", notificationTags);
        // Create post categories
        List<PostCategory> postCategories = postCategoryService.mergeOrCreateByIfAbsent(id, ServiceUtils.fetchProperty(categories, Category::getId));
        log.debug("Created notification categories: [{}]", postCategories);
        applicationContext.publishEvent(new NotificationUpdateEvent(this, notification));
        // get draft content by head patch log id
        Content postContent = postContentService.getById(id);
        notification.setContent(postContentPatchLogService.getPatchedContentById(postContent.getHeadPatchLogId()));

        return notificationAssembler.convertTo(notification, tags, categories);
    }
}