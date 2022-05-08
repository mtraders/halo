package run.halo.app.service.assembler.cern;

import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import run.halo.app.model.dto.CategoryDTO;
import run.halo.app.model.dto.TagDTO;
import run.halo.app.model.dto.cern.notification.NotificationListDTO;
import run.halo.app.model.entity.Category;
import run.halo.app.model.entity.Content;
import run.halo.app.model.entity.Tag;
import run.halo.app.model.entity.cern.Notification;
import run.halo.app.model.vo.cern.notification.NotificationDetailVO;
import run.halo.app.model.vo.cern.notification.NotificationListVO;
import run.halo.app.service.CategoryService;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.PostCategoryService;
import run.halo.app.service.PostTagService;
import run.halo.app.service.TagService;
import run.halo.app.service.assembler.BasePostAssembler;
import run.halo.app.service.assembler.PostAssembler;
import run.halo.app.utils.ServiceUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Notification assembler.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Component
public class NotificationAssembler extends BasePostAssembler<Notification> {

    private final ContentService contentService;
    private final TagService tagService;
    private final CategoryService categoryService;
    private final PostCategoryService postCategoryService;
    private final OptionService optionService;
    private final PostTagService postTagService;
    private final CernAssembler<Notification> cernAssembler;
    private final PostAssembler postAssembler;

    /**
     * constructor of notification assembler.
     *
     * @param contentService content service.
     * @param tagService tag service.
     * @param categoryService category service.
     * @param optionService option service.
     * @param postTagService post tag service.
     * @param postCategoryService post category service.
     * @param cernAssembler cern assembler.
     * @param postAssembler post assembler.
     */
    public NotificationAssembler(ContentService contentService, TagService tagService, CategoryService categoryService, OptionService optionService,
                                 PostTagService postTagService, PostCategoryService postCategoryService,
                                 CernAssembler<Notification> cernAssembler, PostAssembler postAssembler) {
        super(contentService, optionService);
        this.contentService = contentService;
        this.tagService = tagService;
        this.categoryService = categoryService;
        this.optionService = optionService;
        this.postTagService = postTagService;
        this.postCategoryService = postCategoryService;
        this.cernAssembler = cernAssembler;
        this.postAssembler = postAssembler;
    }

    /**
     * convert notification page to notification list vo page.
     *
     * @param notificationPage notification page.
     * @return notification list vo page.
     */
    @NonNull
    public Page<NotificationListVO> convertToListVo(@NonNull Page<Notification> notificationPage) {
        Assert.notNull(notificationPage, "notification page must not be null");
        List<NotificationListVO> notificationListVOList = convertToListVo(notificationPage.getContent());
        Map<Integer, NotificationListVO> notificationListVOMap =
            notificationListVOList.stream().collect(Collectors.toMap(NotificationListVO::getId, Function.identity()));
        return notificationPage.map(notification -> {
            Integer id = notification.getId();
            return notificationListVOMap.get(id);
        });
    }

    /**
     * convert notification entity list to notification list vo.
     *
     * @param notifications notification entities.
     * @return notification list vo.
     */
    public List<NotificationListVO> convertToListVo(List<Notification> notifications) {
        Set<Integer> ids = ServiceUtils.fetchProperty(notifications, Notification::getId);
        // Get tag list map
        Map<Integer, List<Tag>> tagListMap = postTagService.listTagListMapBy(ids);
        // Get category list map
        Map<Integer, List<Category>> categoryListMap = postCategoryService.listCategoryListMap(ids);
        return notifications.stream().map(notification -> {
            NotificationListVO notificationListVO = new NotificationListVO().convertFrom(notification);
            Integer id = notification.getId();
            List<TagDTO> tags =
                Optional.ofNullable(tagListMap.get(id)).orElseGet(LinkedList::new).stream().filter(Objects::nonNull).map(tagService::convertTo)
                    .collect(Collectors.toList());
            notificationListVO.setTags(tags);

            List<CategoryDTO> categories = Optional.ofNullable(categoryListMap.get(id)).orElseGet(LinkedList::new).stream().filter(Objects::nonNull)
                .map(categoryService::convertTo).collect(Collectors.toList());
            notificationListVO.setCategories(categories);
            return notificationListVO;
        }).collect(Collectors.toList());
    }

    /**
     * converts to notification detail vo.
     *
     * @param notification notification.
     * @param tags tag list.
     * @param categories category list.
     * @return notification detail vo.
     */
    @NonNull
    public NotificationDetailVO convertTo(@NonNull Notification notification, @Nullable List<Tag> tags, @Nullable List<Category> categories) {
        Assert.notNull(notification, "notification must not be null");
        NotificationDetailVO detailVO = new NotificationDetailVO().convertFrom(notification);
        cernAssembler.generateAndSetSummaryIfAbsent(notification, detailVO);

        detailVO.setTags(tagService.convertTo(tags));
        detailVO.setCategories(categoryService.convertTo(categories));

        detailVO.setFullPath(postAssembler.buildFullPath(notification));

        Content.PatchedContent newsContent = notification.getContent();
        detailVO.setContent(newsContent.getContent());
        detailVO.setOriginalContent(newsContent.getOriginalContent());

        Boolean inProgress = contentService.draftingInProgress(notification.getId());
        detailVO.setInProgress(inProgress);
        return detailVO;
    }

    /**
     * convert notification to notification vo.
     *
     * @param notification notification entity.
     * @return notification vo.
     */
    @NonNull
    public NotificationDetailVO convertToDetailVo(@NonNull Notification notification) {
        return new NotificationDetailVO().convertFrom(notification);
    }

    /**
     * convert notification entities to notification dtos.
     *
     * @param notifications notification list
     * @return notification dtos.
     */
    public Page<NotificationListDTO> convertToListDTO(Page<Notification> notifications) {
        return null;
    }

}
