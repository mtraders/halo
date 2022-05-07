package run.halo.app.service.assembler.cern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import run.halo.app.model.dto.CategoryDTO;
import run.halo.app.model.dto.TagDTO;
import run.halo.app.model.dto.cern.CernPostListDTO;
import run.halo.app.model.dto.cern.news.NewsDetailDTO;
import run.halo.app.model.dto.cern.news.NewsListDTO;
import run.halo.app.model.entity.Category;
import run.halo.app.model.entity.Content;
import run.halo.app.model.entity.Content.PatchedContent;
import run.halo.app.model.entity.PostMeta;
import run.halo.app.model.entity.Tag;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.vo.cern.news.NewsDetailVO;
import run.halo.app.model.vo.cern.news.NewsListVO;
import run.halo.app.service.CategoryService;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.PostCategoryService;
import run.halo.app.service.PostMetaService;
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
 * News assembler.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Component
public class NewsAssembler extends BasePostAssembler<News> {
    private final ContentService contentService;
    private final TagService tagService;
    private final PostTagService postTagService;
    private final CategoryService categoryService;
    private final PostCategoryService postCategoryService;
    private final PostMetaService postMetaService;
    private final PostAssembler postAssembler;

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
     * @param postAssembler post assembler.
     */
    public NewsAssembler(ContentService contentService, OptionService optionService, TagService tagService, PostTagService postTagService,
                         CategoryService categoryService, PostCategoryService postCategoryService, PostMetaService postMetaService,
                         PostAssembler postAssembler) {
        super(contentService, optionService);
        this.contentService = contentService;
        this.tagService = tagService;
        this.postTagService = postTagService;
        this.categoryService = categoryService;
        this.postCategoryService = postCategoryService;
        this.postMetaService = postMetaService;
        this.postAssembler = postAssembler;
    }

    /**
     * convert new entity page to news list vo page.
     *
     * @param newsPage news page.
     * @return news list vo page.
     */
    @NonNull
    public Page<NewsListVO> convertToListVo(Page<News> newsPage) {
        Assert.notNull(newsPage, "News page must not be null");
        List<NewsListVO> newsListVOList = convertToListVo(newsPage.getContent());
        Map<Integer, NewsListVO> newsListVOMap = newsListVOList.stream().collect(Collectors.toMap(NewsListVO::getId, Function.identity()));
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
    public List<NewsListVO> convertToListVo(List<News> newsList) {
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
            newsListVO.setFullPath(postAssembler.buildFullPath(news));
            return newsListVO;
        }).collect(Collectors.toList());
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
        generateAndSetSummaryIfAbsent(news, newsDetailVO);

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

        newsDetailVO.setFullPath(postAssembler.buildFullPath(news));

        PatchedContent newsContent = news.getContent();
        newsDetailVO.setContent(newsContent.getContent());
        newsDetailVO.setOriginalContent(newsContent.getOriginalContent());

        Boolean inProgress = contentService.draftingInProgress(news.getId());
        newsDetailVO.setInProgress(inProgress);

        return newsDetailVO;
    }

    private <T extends CernPostListDTO<News>> void generateAndSetSummaryIfAbsent(@NonNull News news, @NonNull T newsListDTO) {
        Assert.notNull(news, "The news must not be null.");
        if (StringUtils.isNotBlank(newsListDTO.getSummary())) {
            return;
        }
        PatchedContent patchedContent = news.getContentOfNullable();
        if (patchedContent == null) {
            Content newsContent = contentService.getByIdOfNullable(news.getId());
            if (newsContent != null) {
                newsListDTO.setSummary(generateSummary(newsContent.getContent()));
            } else {
                newsListDTO.setSummary(StringUtils.EMPTY);
            }
        } else {
            newsListDTO.setSummary(generateSummary(patchedContent.getContent()));
        }
    }

    /**
     * convert to news detail vo.
     *
     * @param news news
     * @return news detail vo.
     */
    public NewsDetailVO convertToDetailVo(News news) {
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
        generateAndSetSummaryIfAbsent(news, newsListDTO);

        // Post currently drafting in process
        Boolean isInProcess = contentService.draftingInProgress(news.getId());
        newsListDTO.setInProgress(isInProcess);
        return newsListDTO;
    }

    @NonNull
    public Page<NewsListDTO> convertToListDTO(@NonNull Page<News> newsPage) {
        Assert.notNull(newsPage, "News page cannot be null");
        return newsPage.map(this::convertToListDTO);
    }

}
