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
import run.halo.app.event.cern.PaperUpdateEvent;
import run.halo.app.event.logger.LogEvent;
import run.halo.app.model.entity.Category;
import run.halo.app.model.entity.Content;
import run.halo.app.model.entity.PostCategory;
import run.halo.app.model.entity.PostTag;
import run.halo.app.model.entity.Tag;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.model.entity.cern.Personnel;
import run.halo.app.model.entity.cern.PostPersonnel;
import run.halo.app.model.enums.LogType;
import run.halo.app.model.params.cern.paper.PaperQuery;
import run.halo.app.model.vo.cern.paper.PaperDetailVO;
import run.halo.app.repository.cern.PaperRepository;
import run.halo.app.service.CategoryService;
import run.halo.app.service.ContentPatchLogService;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.PostCategoryService;
import run.halo.app.service.PostTagService;
import run.halo.app.service.TagService;
import run.halo.app.service.assembler.cern.PaperAssembler;
import run.halo.app.service.cern.PaperService;
import run.halo.app.service.cern.PersonnelService;
import run.halo.app.service.cern.PostPersonnelService;
import run.halo.app.service.impl.BasePostServiceImpl;
import run.halo.app.utils.DateUtils;
import run.halo.app.utils.ServiceUtils;

import java.util.List;
import java.util.Set;


/**
 * paper service impl.
 *
 * @author lizc
 */
@Slf4j
@Service
public class PaperServiceImpl extends BasePostServiceImpl<Paper> implements PaperService {

    private final PaperRepository paperRepository;
    private final TagService tagService;
    private final CategoryService categoryService;
    private final PersonnelService personnelService;
    private final ContentPatchLogService contentPatchLogService;
    private final PaperAssembler paperAssembler;
    private final PostTagService postTagService;
    private final PostCategoryService postCategoryService;
    private final PostPersonnelService postPersonnelService;
    private final ApplicationEventPublisher eventPublisher;
    private final ApplicationContext applicationContext;
    private final ContentService contentService;

    /**
     * base post repository constructor.
     *
     * @param paperRepository paper repository.
     * @param optionService option service.
     * @param contentService content service.
     * @param tagService tag service.
     * @param categoryService category service.
     * @param personnelService personnel service.
     * @param contentPatchLogService content path log service.
     * @param paperAssembler paper assembler.
     * @param postTagService post tag service.
     * @param postCategoryService post category service.
     * @param postPersonnelService post personnel service.
     * @param eventPublisher event publisher.
     * @param applicationContext application context
     */
    public PaperServiceImpl(PaperRepository paperRepository, OptionService optionService, ContentService contentService, TagService tagService,
                            CategoryService categoryService, PersonnelService personnelService, ContentPatchLogService contentPatchLogService,
                            PaperAssembler paperAssembler, PostTagService postTagService, PostCategoryService postCategoryService,
                            PostPersonnelService postPersonnelService, ApplicationEventPublisher eventPublisher,
                            ApplicationContext applicationContext) {
        super(paperRepository, optionService, contentService, contentPatchLogService);
        this.paperRepository = paperRepository;
        this.tagService = tagService;
        this.categoryService = categoryService;
        this.personnelService = personnelService;
        this.contentPatchLogService = contentPatchLogService;
        this.paperAssembler = paperAssembler;
        this.postTagService = postTagService;
        this.postCategoryService = postCategoryService;
        this.postPersonnelService = postPersonnelService;
        this.eventPublisher = eventPublisher;
        this.applicationContext = applicationContext;
        this.contentService = contentService;
    }

    /**
     * Get post with the latest content by id.
     * content from patch log.
     *
     * @param postId post id.
     * @return post with the latest content.
     */
    @Override
    public Paper getWithLatestContentById(Integer postId) {
        Paper paper = getById(postId);
        Content content = getContentById(postId);
        Content.PatchedContent patchedContent = contentPatchLogService.getPatchedContentById(content.getHeadPatchLogId());
        paper.setContent(patchedContent);
        return paper;
    }

    /**
     * Page paper.
     *
     * @param paperQuery paper query.
     * @param pageable page info.
     * @return paper page.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @NonNull
    public Page<Paper> pageBy(@NonNull PaperQuery paperQuery, @NonNull Pageable pageable) {
        Assert.notNull(paperQuery, "Paper query must not be null");
        Assert.notNull(pageable, "Paper page info must not be null");
        Specification<Paper> paperSpecification = paperAssembler.buildSpecByQuery(paperQuery);
        return paperRepository.findAll(paperSpecification, pageable);
    }

    /**
     * create paper by paper param.
     *
     * @param paper paper entity.
     * @param tagIds tag id list.
     * @param categoryIds category id list.
     * @param authorIds author id list.
     * @param autoSave auto-save or not
     * @return paper detail vo.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @NonNull
    public PaperDetailVO createBy(@NonNull Paper paper, Set<Integer> tagIds, Set<Integer> categoryIds, Set<Integer> authorIds, boolean autoSave) {
        PaperDetailVO detailVO = createOrUpdate(paper, tagIds, categoryIds, authorIds);
        if (!autoSave) {
            LogEvent logEvent = new LogEvent(this, detailVO.getId().toString(), LogType.PAPER_PUBLISHED, detailVO.getTitle());
            eventPublisher.publishEvent(logEvent);
        }
        return detailVO;
    }

    /**
     * update paper by paper param.
     *
     * @param paper paper entity.
     * @param tagIds tag id list.
     * @param categoryIds category id list.
     * @param authorIds author id list.
     * @param autoSave auto-save or not
     * @return paper detail vo.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @NonNull
    public PaperDetailVO updateBy(@NonNull Paper paper, Set<Integer> tagIds, Set<Integer> categoryIds, Set<Integer> authorIds, boolean autoSave) {
        paper.setEditTime(DateUtils.now());
        PaperDetailVO detailVO = createOrUpdate(paper, tagIds, categoryIds, authorIds);
        if (!autoSave) {
            LogEvent logEvent = new LogEvent(this, detailVO.getId().toString(), LogType.PAPER_PUBLISHED, detailVO.getTitle());
            eventPublisher.publishEvent(logEvent);
        }
        return detailVO;
    }

    private PaperDetailVO createOrUpdate(@NonNull Paper paper, Set<Integer> tagIds, Set<Integer> categoryIds, Set<Integer> authorIds) {
        Assert.notNull(paper, "Paper must not be null");
        paper = super.createOrUpdateBy(paper);
        Integer paperId = paper.getId();
        postTagService.removeByPostId(paperId);
        postCategoryService.removeByPostId(paperId);
        postPersonnelService.removeByPostId(paperId);

        // List all tags
        List<Tag> tags = tagService.listAllByIds(tagIds);
        // List all categories
        List<Category> categories = categoryService.listAllByIds(categoryIds);
        // List all personnel
        List<Personnel> authors = personnelService.listAllByIds(authorIds);

        List<PostTag> paperTags = postTagService.mergeOrCreateByIfAbsent(paperId, ServiceUtils.fetchProperty(tags, Tag::getId));
        log.debug("Created paper tags: [{}]", paperTags);
        List<PostCategory> paperCategories =
            postCategoryService.mergeOrCreateByIfAbsent(paperId, ServiceUtils.fetchProperty(categories, Category::getId));
        log.debug("Created paper categories: [{}]", paperCategories);
        List<PostPersonnel> paperAuthors =
            postPersonnelService.mergeOrCreateByIfAbsent(paperId, ServiceUtils.fetchProperty(authors, Personnel::getId));
        log.debug("Created paper authors: [{}]", paperAuthors);

        applicationContext.publishEvent(new PaperUpdateEvent(this, paper));

        // get draft content by head patch log id
        Content postContent = contentService.getById(paperId);
        paper.setContent(contentPatchLogService.getPatchedContentById(postContent.getHeadPatchLogId()));
        return paperAssembler.convertTo(paper, tags, categories, authors);
    }
}
