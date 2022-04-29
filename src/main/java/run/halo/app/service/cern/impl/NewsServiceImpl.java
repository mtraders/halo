package run.halo.app.service.cern.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import run.halo.app.event.cern.NewsUpdateEvent;
import run.halo.app.event.logger.LogEvent;
import run.halo.app.model.entity.Category;
import run.halo.app.model.entity.Content;
import run.halo.app.model.entity.PostCategory;
import run.halo.app.model.entity.PostMeta;
import run.halo.app.model.entity.PostTag;
import run.halo.app.model.entity.Tag;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.enums.LogType;
import run.halo.app.model.vo.cern.news.NewsDetailVO;
import run.halo.app.repository.cern.NewsRepository;
import run.halo.app.service.CategoryService;
import run.halo.app.service.ContentPatchLogService;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.PostCategoryService;
import run.halo.app.service.PostMetaService;
import run.halo.app.service.PostTagService;
import run.halo.app.service.TagService;
import run.halo.app.service.assembler.cern.NewsAssembler;
import run.halo.app.service.cern.NewsService;
import run.halo.app.service.impl.BasePostServiceImpl;
import run.halo.app.utils.DateUtils;
import run.halo.app.utils.ServiceUtils;

import java.util.List;
import java.util.Set;

/**
 * News service impl.
 *
 * @author <a href="lizc@fists.cn">lizc</a>
 */
@Slf4j
@Service
public class NewsServiceImpl extends BasePostServiceImpl<News> implements NewsService {

    private final NewsRepository newsRepository;
    private final ContentService contentService;
    private final ContentPatchLogService contentPatchLogService;
    private final OptionService optionService;
    private final ApplicationEventPublisher eventPublisher;
    private final NewsAssembler newsAssembler;
    private final PostTagService postTagService;
    private final PostCategoryService postCategoryService;
    private final TagService tagService;
    private final CategoryService categoryService;
    private final ApplicationContext applicationContext;
    private final PostMetaService postMetaService;
    private final ContentService postContentService;
    private final ContentPatchLogService postContentPatchLogService;

    /**
     * constructor of news service impl.
     *
     * @param newsRepository news repository.
     * @param optionService option service.
     * @param contentService content service.
     * @param contentPatchLogService content patch log service.
     * @param eventPublisher application event publisher.
     * @param newsAssembler news assembler.
     * @param postTagService post tag service.
     * @param postCategoryService post category service.
     * @param tagService tag service.
     * @param categoryService category service.
     * @param applicationContext application context.
     * @param postMetaService post meta service.
     * @param postContentService post content service.
     * @param postContentPatchLogService post content patch log service.
     */
    public NewsServiceImpl(NewsRepository newsRepository, OptionService optionService, ContentService contentService,
                           ContentPatchLogService contentPatchLogService, ApplicationEventPublisher eventPublisher, NewsAssembler newsAssembler,
                           PostTagService postTagService, PostCategoryService postCategoryService, TagService tagService,
                           CategoryService categoryService, ApplicationContext applicationContext, PostMetaService postMetaService,
                           ContentService postContentService, ContentPatchLogService postContentPatchLogService) {
        super(newsRepository, optionService, contentService, contentPatchLogService);
        this.newsRepository = newsRepository;
        this.contentService = contentService;
        this.contentPatchLogService = contentPatchLogService;
        this.optionService = optionService;
        this.eventPublisher = eventPublisher;
        this.newsAssembler = newsAssembler;
        this.postTagService = postTagService;
        this.postCategoryService = postCategoryService;
        this.tagService = tagService;
        this.categoryService = categoryService;
        this.applicationContext = applicationContext;
        this.postMetaService = postMetaService;
        this.postContentService = postContentService;
        this.postContentPatchLogService = postContentPatchLogService;
    }


    /**
     * Get post with the latest content by id.
     * content from patch log.
     *
     * @param newsId post id.
     * @return post with the latest content.
     */
    @Override
    public News getWithLatestContentById(Integer newsId) {
        News news = getById(newsId);
        Content newsContent = getContentById(newsId);
        // Use the head pointer stored in the post content.
        Content.PatchedContent patchedContent = postContentPatchLogService.getPatchedContentById(newsContent.getHeadPatchLogId());
        news.setContent(patchedContent);
        return news;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @NonNull
    public NewsDetailVO createBy(@NonNull News newsToCreate, Set<Integer> tagIds, Set<Integer> categoryIds, Set<PostMeta> metas, boolean autoSave) {
        NewsDetailVO createdNews = createOrUpdate(newsToCreate, tagIds, categoryIds, metas);
        if (!autoSave) {
            LogEvent logEvent = new LogEvent(this, createdNews.getId().toString(), LogType.NEWS_PUBLISHED, createdNews.getTitle());
            eventPublisher.publishEvent(logEvent);
        }
        return createdNews;
    }

    /**
     * Updates news by news, tag id set and category id set.
     *
     * @param newsToUpdate news to update
     * @param tagIds tag id set
     * @param categoryIds category id set
     * @param metas metas
     * @param autoSave auto save
     * @return updated news
     */
    @Override
    @NonNull
    public NewsDetailVO updateBy(@NonNull News newsToUpdate, Set<Integer> tagIds, Set<Integer> categoryIds, Set<PostMeta> metas, boolean autoSave) {
        newsToUpdate.setEditTime(DateUtils.now());
        NewsDetailVO updatedNews = createOrUpdate(newsToUpdate, tagIds, categoryIds, metas);
        if (!autoSave) {
            // Log the creation
            LogEvent logEvent = new LogEvent(this, updatedNews.getId().toString(), LogType.NEWS_PUBLISHED, updatedNews.getTitle());
            eventPublisher.publishEvent(logEvent);
        }
        return updatedNews;
    }

    private NewsDetailVO createOrUpdate(@NonNull News news, Set<Integer> tagIds, Set<Integer> categoryIds, Set<PostMeta> metas) {
        Assert.notNull(news, "News must not be null");
        news = super.createOrUpdateBy(news);

        Integer newsId = news.getId();
        postTagService.removeByPostId(newsId);
        postCategoryService.removeByPostId(newsId);

        // List all tags
        List<Tag> tags = tagService.listAllByIds(tagIds);
        // List all categories
        List<Category> categories = categoryService.listAllByIds(categoryIds);
        // Create post tags
        List<PostTag> newsTags = postTagService.mergeOrCreateByIfAbsent(newsId, ServiceUtils.fetchProperty(tags, Tag::getId));
        log.debug("Created news tags: [{}]", newsTags);
        // Create post categories
        List<PostCategory> postCategories =
            postCategoryService.mergeOrCreateByIfAbsent(newsId, ServiceUtils.fetchProperty(categories, Category::getId));

        log.debug("Created news categories: [{}]", postCategories);

        // Create post meta data
        List<PostMeta> postMetaList = postMetaService.createOrUpdateByPostId(newsId, metas);
        log.debug("Created news metas: [{}]", postMetaList);

        applicationContext.publishEvent(new NewsUpdateEvent(this, news));

        // get draft content by head patch log id
        Content postContent = postContentService.getById(newsId);
        news.setContent(postContentPatchLogService.getPatchedContentById(postContent.getHeadPatchLogId()));
        // Convert to news detail vo
        return newsAssembler.convertTo(news, tags, categories, postMetaList);
    }
}
