package run.halo.app.service.cern.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import run.halo.app.event.cern.NewsUpdateEvent;
import run.halo.app.event.logger.LogEvent;
import run.halo.app.model.entity.Category;
import run.halo.app.model.entity.Content;
import run.halo.app.model.entity.Post;
import run.halo.app.model.entity.PostCategory;
import run.halo.app.model.entity.PostMeta;
import run.halo.app.model.entity.PostTag;
import run.halo.app.model.entity.Tag;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.enums.LogType;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.params.PostQuery;
import run.halo.app.model.properties.PostProperties;
import run.halo.app.model.vo.cern.news.NewsDetailVO;
import run.halo.app.repository.cern.NewsRepository;
import run.halo.app.service.CategoryService;
import run.halo.app.service.ContentPatchLogService;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.PostCategoryService;
import run.halo.app.service.PostCommentService;
import run.halo.app.service.PostMetaService;
import run.halo.app.service.PostTagService;
import run.halo.app.service.TagService;
import run.halo.app.service.assembler.cern.NewsAssembler;
import run.halo.app.service.cern.NewsService;
import run.halo.app.service.impl.BasePostServiceImpl;
import run.halo.app.utils.DateUtils;
import run.halo.app.utils.ServiceUtils;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

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
    private final PostCommentService postCommentService;

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
     * @param postCommentService post comment service.
     */
    public NewsServiceImpl(NewsRepository newsRepository, OptionService optionService, ContentService contentService,
                           ContentPatchLogService contentPatchLogService, ApplicationEventPublisher eventPublisher, NewsAssembler newsAssembler,
                           PostTagService postTagService, PostCategoryService postCategoryService, TagService tagService,
                           CategoryService categoryService, ApplicationContext applicationContext, PostMetaService postMetaService,
                           ContentService postContentService, ContentPatchLogService postContentPatchLogService,
                           PostCommentService postCommentService) {
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
        this.postCommentService = postCommentService;
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

    /**
     * pages news.
     *
     * @param postQuery post query.
     * @param pageable pageable.
     * @return news list vo.
     */
    @NonNull
    public Page<News> pageBy(@NonNull PostQuery postQuery, @NonNull Pageable pageable) {
        Assert.notNull(postQuery, "Post query must not be null");
        Assert.notNull(pageable, "Pageable must not be null");
        return newsRepository.findAll(buildSpecByQuery(postQuery), pageable);
    }

    /**
     * Pages news by keyword.
     *
     * @param keyword keyword
     * @param pageable pageable
     * @return a page of news
     */
    @NonNull
    public Page<News> pageBy(@NonNull String keyword, @NonNull Pageable pageable) {
        Assert.notNull(keyword, "Keyword must not be null");
        Assert.notNull(pageable, "Pageable must not be null");

        PostQuery postQuery = new PostQuery();
        postQuery.setKeyword(keyword);
        postQuery.setStatuses(Set.of(PostStatus.PUBLISHED));

        // Build specification and find all
        return newsRepository.findAll(buildSpecByQuery(postQuery), pageable);
    }

    @NonNull
    private Specification<News> buildSpecByQuery(@NonNull PostQuery postQuery) {
        Assert.notNull(postQuery, "News query must not be null");
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();

            Set<PostStatus> statuses = postQuery.getStatuses();
            if (!CollectionUtils.isEmpty(statuses)) {
                predicates.add(root.get("status").in(statuses));
            }

            if (postQuery.getCategoryId() != null) {
                List<Integer> categoryIds =
                    categoryService.listAllByParentId(postQuery.getCategoryId()).stream().map(Category::getId).collect(Collectors.toList());
                Subquery<Post> postSubquery = query.subquery(Post.class);
                Root<PostCategory> postCategoryRoot = postSubquery.from(PostCategory.class);
                postSubquery.select(postCategoryRoot.get("postId"));
                postSubquery.where(criteriaBuilder.equal(root.get("id"), postCategoryRoot.get("postId")),
                    postCategoryRoot.get("categoryId").in(categoryIds));
                predicates.add(criteriaBuilder.exists(postSubquery));
            }

            if (postQuery.getKeyword() != null) {

                // Format like condition
                String likeCondition = String.format("%%%s%%", StringUtils.strip(postQuery.getKeyword()));

                // Build like predicate
                Subquery<News> postSubquery = query.subquery(News.class);
                Root<Content> contentRoot = postSubquery.from(Content.class);
                postSubquery.select(contentRoot.get("id")).where(criteriaBuilder.like(contentRoot.get("originalContent"), likeCondition));

                Predicate titleLike = criteriaBuilder.like(root.get("title"), likeCondition);

                predicates.add(criteriaBuilder.or(titleLike, criteriaBuilder.in(root).value(postSubquery)));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
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

    /**
     * Remove news in batch.
     *
     * @param ids ids must not be null.
     * @return a list of deleted news.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @NonNull
    public List<News> removeByIds(@NonNull Collection<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return ids.stream().map(this::removeById).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @NonNull
    public News removeById(@NonNull Integer newsId) {
        Assert.notNull(newsId, "News id must not be null");
        log.debug("Removing news: {}", newsId);
        // Remove news tags
        List<PostTag> newsTags = postTagService.removeByPostId(newsId);
        log.debug("Removed news tags: [{}]", newsTags);
        // Remove news categories
        List<PostCategory> newsCategories = postCategoryService.removeByPostId(newsId);
        log.debug("Remove news categories: [{}]", newsCategories);
        // Remove metas
        List<PostMeta> metas = postMetaService.removeByPostId(newsId);
        log.debug("Removed news metas: [{}]", metas);
        // Remove news comments
        postCommentService.removeByPostId(newsId);
        // Remove news content
        Content newsContent = postContentService.removeById(newsId);
        log.debug("Removed news content: [{}]", newsContent);

        News deletedNews = super.removeById(newsId);
        deletedNews.setContent(Content.PatchedContent.of(newsContent));

        // Log it
        eventPublisher.publishEvent(new LogEvent(this, newsId.toString(), LogType.NEWS_DELETED, deletedNews.getTitle()));

        return deletedNews;
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

    @Override
    @NonNull
    public Optional<News> getPrevPost(@NonNull News news) {
        List<News> newsList = listPrevPosts(news, 1);
        return CollectionUtils.isEmpty(newsList) ? Optional.empty() : Optional.of(newsList.get(0));
    }

    @Override
    @NonNull
    public Optional<News> getNextPost(@NonNull News news) {
        List<News> newsList = listNextPosts(news, 1);
        return CollectionUtils.isEmpty(newsList) ? Optional.empty() : Optional.of(newsList.get(0));
    }

    @Override
    @NonNull
    public List<News> listNextPosts(@NonNull News news, int size) {
        return listFollowingNewsByDirection(news, ASC, size);
    }

    @Override
    @NonNull
    public List<News> listPrevPosts(@NonNull News news, int size) {
        return listFollowingNewsByDirection(news, DESC, size);
    }

    private List<News> listFollowingNewsByDirection(@NonNull News news, Direction direction, int size) {
        Assert.notNull(news, "News must not be null");
        String indexSort = optionService.getByPropertyOfNonNull(PostProperties.INDEX_SORT).toString();
        PageRequest pageRequest = PageRequest.of(0, size, Sort.by(direction, indexSort));
        switch (indexSort) {
            case "createTime":
                return newsRepository.findAllByStatusAndCreateTimeAfter(PostStatus.PUBLISHED, news.getCreateTime(), pageRequest).getContent();
            case "editTime":
                return newsRepository.findAllByStatusAndEditTimeAfter(PostStatus.PUBLISHED, news.getEditTime(), pageRequest).getContent();
            case "visits":
                return newsRepository.findAllByStatusAndVisitsAfter(PostStatus.PUBLISHED, news.getVisits(), pageRequest).getContent();
            default:
                return Collections.emptyList();
        }
    }
}
