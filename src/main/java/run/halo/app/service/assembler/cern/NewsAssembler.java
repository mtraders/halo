package run.halo.app.service.assembler.cern;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import run.halo.app.model.dto.CategoryDTO;
import run.halo.app.model.dto.TagDTO;
import run.halo.app.model.dto.cern.news.NewsListDTO;
import run.halo.app.model.entity.Category;
import run.halo.app.model.entity.Content.PatchedContent;
import run.halo.app.model.entity.PostMeta;
import run.halo.app.model.entity.Tag;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.params.cern.NewsQuery;
import run.halo.app.model.vo.cern.news.NewsDetailVO;
import run.halo.app.model.vo.cern.news.NewsListVO;
import run.halo.app.service.CategoryService;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.PostCategoryService;
import run.halo.app.service.PostMetaService;
import run.halo.app.service.PostTagService;
import run.halo.app.service.TagService;
import run.halo.app.utils.ServiceUtils;

import javax.persistence.criteria.Predicate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * News assembler.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Component
public class NewsAssembler extends CernPostAssembler<News> {
    private final TagService tagService;
    private final PostTagService postTagService;
    private final CategoryService categoryService;
    private final PostCategoryService postCategoryService;
    private final PostMetaService postMetaService;

    /**
     * news assembler constructor.
     *
     * @param contentService content service.
     * @param optionService option service.
     * @param tagService tag service.
     * @param postTagService post tag service.
     * @param categoryService category service.
     * @param postCategoryService post category service.
     * @param postMetaService post meta service.
     */
    public NewsAssembler(ContentService contentService, OptionService optionService, TagService tagService, PostTagService postTagService,
                         CategoryService categoryService, PostCategoryService postCategoryService, PostMetaService postMetaService) {
        super(categoryService, contentService, optionService);
        this.tagService = tagService;
        this.postTagService = postTagService;
        this.categoryService = categoryService;
        this.postCategoryService = postCategoryService;
        this.postMetaService = postMetaService;
    }

    /**
     * convert new entity page to news list vo page.
     *
     * @param newsPage news page.
     * @return news list vo page.
     */
    @NonNull
    public Page<NewsListVO> convertToListVO(Page<News> newsPage) {
        Assert.notNull(newsPage, "News page must not be null");
        List<NewsListVO> newsListVOList = convertToListVO(newsPage.getContent());
        Map<Integer, NewsListVO> newsListVOMap = ServiceUtils.convertToMap(newsListVOList, NewsListVO::getId);
        return newsPage.map(news -> {
            Integer newsId = news.getId();
            return newsListVOMap.get(newsId);
        });
    }

    /**
     * convert news-list list to news-list vo list.
     *
     * @param newsList news list.
     * @return news list vo list.
     */
    public List<NewsListVO> convertToListVO(List<News> newsList) {
        Set<Integer> newsIds = ServiceUtils.fetchProperty(newsList, News::getId);
        // Get tag list map
        Map<Integer, List<Tag>> tagListMap = postTagService.listTagListMapBy(newsIds);
        // Get category list map
        Map<Integer, List<Category>> categoryListMap = postCategoryService.listCategoryListMap(newsIds);
        // Get post meta list map
        Map<Integer, List<PostMeta>> postMetaListMap = postMetaService.listPostMetaAsMap(newsIds);
        return newsList.stream().map(news -> {
            NewsListVO newsListVO = new NewsListVO().convertFrom(news);

            Integer newsId = news.getId();
            List<TagDTO> tags =
                Optional.ofNullable(tagListMap.get(newsId)).orElseGet(LinkedList::new).stream().filter(Objects::nonNull).map(tagService::convertTo)
                    .collect(Collectors.toList());
            newsListVO.setTags(tags);

            List<CategoryDTO> categories =
                Optional.ofNullable(categoryListMap.get(newsId)).orElseGet(LinkedList::new).stream().filter(Objects::nonNull)
                    .map(categoryService::convertTo).collect(Collectors.toList());
            newsListVO.setCategories(categories);

            List<PostMeta> metas = Optional.ofNullable(postMetaListMap.get(newsId)).orElseGet(LinkedList::new).stream().filter(Objects::nonNull)
                .collect(Collectors.toList());
            newsListVO.setMetas(postMetaService.convertToMap(metas));
            newsListVO.setFullPath(buildFullPath(news));
            return newsListVO;
        }).collect(Collectors.toList());
    }

    /**
     * convert news entity to list vo.
     *
     * @param news news entity.
     * @return news list vo.
     */
    @NonNull
    public NewsListVO convertToListVO(@NonNull News news) {
        Integer id = news.getId();
        List<Tag> tags = postTagService.listTagsBy(id);
        List<Category> categories = postCategoryService.listCategoriesBy(id);
        List<PostMeta> metas = postMetaService.listBy(id);
        NewsListVO newsListVO = new NewsListVO().convertFrom(news);
        newsListVO.setTags(tagService.convertTo(tags));
        newsListVO.setCategories(categoryService.convertTo(categories));
        newsListVO.setMetas(postMetaService.convertToMap(metas));
        newsListVO.setFullPath(buildFullPath(news));
        return newsListVO;
    }

    /**
     * Converts to news detail vo.
     *
     * @param news news must not be null.
     * @param tags tags
     * @param categories categories.
     * @param postMetaList meta list
     * @return news detail
     */
    @NonNull
    public NewsDetailVO convertTo(@NonNull News news, @Nullable List<Tag> tags, @Nullable List<Category> categories, List<PostMeta> postMetaList) {
        Assert.notNull(news, "news must not be null");

        NewsDetailVO newsDetailVO = new NewsDetailVO().convertFrom(news);
        generateAndSetDTOInfoIfAbsent(news, newsDetailVO);

        // Extract ids
        Set<Integer> tagIds = ServiceUtils.fetchProperty(tags, Tag::getId);
        Set<Integer> categoryIds = ServiceUtils.fetchProperty(categories, Category::getId);
        Set<Long> metaIds = ServiceUtils.fetchProperty(postMetaList, PostMeta::getId);

        // Get post tag ids
        newsDetailVO.setTagIds(tagIds);
        newsDetailVO.setTags(tagService.convertTo(tags));

        // Get post category ids
        newsDetailVO.setCategoryIds(categoryIds);
        newsDetailVO.setCategories(categoryService.convertTo(categories));

        // Get post meta ids
        newsDetailVO.setMetaIds(metaIds);
        newsDetailVO.setMetas(postMetaService.convertTo(postMetaList));

        PatchedContent newsContent = news.getContent();
        newsDetailVO.setContent(newsContent.getContent());
        newsDetailVO.setOriginalContent(newsContent.getOriginalContent());

        return newsDetailVO;
    }

    /**
     * convert to news detail vo.
     *
     * @param news news
     * @return news detail vo.
     */
    public NewsDetailVO convertToDetailVO(News news) {
        // List tags
        List<Tag> tags = postTagService.listTagsBy(news.getId());
        // List categories
        List<Category> categories = postCategoryService.listCategoriesBy(news.getId());
        // List metas
        List<PostMeta> metas = postMetaService.listBy(news.getId());
        // Convert to detail vo
        return convertTo(news, tags, categories, metas);
    }

    /**
     * convert to list dto.
     *
     * @param news news entity
     * @return news list dto.
     */
    @NonNull
    public NewsListDTO convertToListDTO(News news) {
        Assert.notNull(news, "News must not be null");
        NewsListDTO newsListDTO = new NewsListDTO().convertFrom(news);
        generateAndSetDTOInfoIfAbsent(news, newsListDTO);
        return newsListDTO;
    }

    /**
     * convert to list dto page.
     *
     * @param newsPage news entity page
     * @return news list dto.
     */
    @NonNull
    public Page<NewsListDTO> convertToListDTO(@NonNull Page<News> newsPage) {
        Assert.notNull(newsPage, "News page cannot be null");
        return newsPage.map(this::convertToListDTO);
    }

    /**
     * build cern spec by query.
     *
     * @return Specification
     */
    public Specification<News> buildSpecByQuery(NewsQuery newsQuery) {
        Specification<News> newsSpecification = super.buildSpecByQuery(newsQuery, News.class);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = Lists.newLinkedList();
            Predicate predicate = newsSpecification.toPredicate(root, query, criteriaBuilder);
            predicates.add(predicate);
            // add source
            if (StringUtils.isNotBlank(newsQuery.getSource())) {
                predicates.add(criteriaBuilder.equal(root.get("source"), newsQuery.getSource()));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
    }
}
