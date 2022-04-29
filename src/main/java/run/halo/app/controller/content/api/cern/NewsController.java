package run.halo.app.controller.content.api.cern;

import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.params.PostQuery;
import run.halo.app.model.vo.cern.news.NewsDetailVO;
import run.halo.app.model.vo.cern.news.NewsListVO;
import run.halo.app.service.assembler.cern.NewsAssembler;
import run.halo.app.service.cern.NewsService;

import java.util.Set;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * News content controller.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@RestController("ApiContentCernNewsController")
@RequestMapping(value = "/api/content/cern/news")
public class NewsController {

    private final NewsService newsService;
    private final NewsAssembler newsAssembler;

    /**
     * News content controller constructor.
     *
     * @param newsService news service.
     * @param newsAssembler news assembler.
     */
    public NewsController(NewsService newsService, NewsAssembler newsAssembler) {
        this.newsService = newsService;
        this.newsAssembler = newsAssembler;
    }

    /**
     * Enable users search published news with keywords.
     *
     * @param pageable sort by priority and create time.
     * @param keyword search news with keyword
     * @param categoryId search news with category id
     * @return published news that contain keywords and specific categoryId
     */
    @GetMapping
    @ApiOperation("Lists news")
    public Page<NewsListVO> pageBy(@PageableDefault(sort = {"topPriority", "createTime"}, direction = DESC) Pageable pageable,
                                   @RequestParam(value = "keyword", required = false) String keyword,
                                   @RequestParam(value = "categoryId", required = false) Integer categoryId) {
        PostQuery postQuery = new PostQuery();
        postQuery.setKeyword(keyword);
        postQuery.setCategoryId(categoryId);
        postQuery.setStatuses(Set.of(PostStatus.PUBLISHED));
        Page<News> newsPage = newsService.pageBy(postQuery, pageable);
        return newsAssembler.convertToListVo(newsPage);
    }

    /**
     * List news by keyword.
     *
     * @param keyword keyword
     * @param pageable pageable
     * @return news list vo.
     */
    @PostMapping(value = "search")
    @ApiOperation("Lists news by keyword")
    public Page<NewsListVO> pageBy(@RequestParam(value = "keyword") String keyword,
                                   @PageableDefault(sort = "createTime", direction = DESC) Pageable pageable) {
        Page<News> newsPage = newsService.pageBy(keyword, pageable);
        return newsAssembler.convertToListVo(newsPage);
    }

    @GetMapping("{newsId:\\d+}")
    @ApiOperation("Get a news")
    public NewsDetailVO getBy(@PathVariable("newsId") Integer newsId) {
        return null;
    }

    @GetMapping("/slug")
    @ApiOperation("Get a news by slug")
    public NewsDetailVO getBy(@RequestParam("slug") String slug) {
        return null;
    }

    @GetMapping("{newsId:\\d+}/prev")
    @ApiOperation("Get prev news by current post id")
    public NewsDetailVO getPrevNewsBy(@PathVariable("newsId") Integer newsId) {
        return null;
    }

    @GetMapping("{newsId:\\d+}/next")
    @ApiOperation("Get next news by current post id")
    public NewsDetailVO getNextNewsBy(@PathVariable("newsId") Integer newsId) {
        return null;
    }
}
