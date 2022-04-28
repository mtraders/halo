package run.halo.app.service.assembler.cern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
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
import run.halo.app.service.PostMetaService;
import run.halo.app.service.TagService;
import run.halo.app.service.assembler.BasePostAssembler;
import run.halo.app.service.assembler.PostAssembler;
import run.halo.app.utils.ServiceUtils;

import java.util.List;
import java.util.Set;

@Component
public class NewsAssembler extends BasePostAssembler<News> {
    private final ContentService contentService;
    private final TagService tagService;
    private final CategoryService categoryService;
    private final PostMetaService postMetaService;
    private final PostAssembler postAssembler;

    public NewsAssembler(ContentService contentService, OptionService optionService, TagService tagService,
                         CategoryService categoryService, PostMetaService postMetaService, PostAssembler postAssembler) {
        super(contentService, optionService);
        this.contentService = contentService;
        this.tagService = tagService;
        this.categoryService = categoryService;
        this.postMetaService = postMetaService;
        this.postAssembler = postAssembler;
    }

    @NonNull
    public Page<NewsListVO> convertToListVo(Page<News> newsPage) {
        Assert.notNull(newsPage, "News page must not be null");
        return newsPage.map(news -> {
            NewsListVO newsListVO = new NewsListVO();
            return newsListVO;
        });
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

        return newsDetailVO;
    }

    private void generateAndSetSummaryIfAbsent(News news, NewsDetailVO newsDetailVO) {
        Assert.notNull(news, "The news must not be null.");
        if (StringUtils.isNotBlank(newsDetailVO.getSummary())) {
            return;
        }
        PatchedContent patchedContent = news.getContentOfNullable();
        if (patchedContent == null) {
            Content newsContent = contentService.getByIdOfNullable(news.getId());
            if (newsContent != null) {
                newsDetailVO.setSummary(generateSummary(newsContent.getContent()));
            } else {
                newsDetailVO.setSummary(StringUtils.EMPTY);
            }
        } else {
            newsDetailVO.setSummary(generateSummary(patchedContent.getContent()));
        }
    }

}
