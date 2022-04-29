package run.halo.app.controller.admin.api.cern;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import run.halo.app.cache.AbstractCacheStore;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.params.PostContentParam;
import run.halo.app.model.params.cern.NewsParam;
import run.halo.app.model.vo.cern.news.NewsDetailVO;
import run.halo.app.model.vo.cern.news.NewsListVO;
import run.halo.app.service.OptionService;
import run.halo.app.service.assembler.cern.NewsAssembler;
import run.halo.app.service.cern.NewsService;

import javax.validation.Valid;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * News controller.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@RestController
@RequestMapping("/api/admin/cern/news")
public class NewsController {

    private final NewsService newsService;
    private final AbstractCacheStore cacheStore;
    private final OptionService optionService;
    private final NewsAssembler newsAssembler;

    public NewsController(NewsService newsService, AbstractCacheStore cacheStore, OptionService optionService, NewsAssembler newsAssembler) {
        this.newsService = newsService;
        this.cacheStore = cacheStore;
        this.optionService = optionService;
        this.newsAssembler = newsAssembler;
    }

    @GetMapping
    @ApiOperation("Get a page of news")
    public Page<NewsListVO> pageBy(@PageableDefault(sort = "createTime", direction = DESC) Pageable pageable) {
        Page<News> newsPage = newsService.pageBy(pageable);
        return newsAssembler.convertToListVo(newsPage);
    }


    @GetMapping("{newsId:\\d+}")
    @ApiOperation("Get a news")
    public NewsDetailVO getBy(@PathVariable("newsId") Integer newsId) {
        News news = newsService.getWithLatestContentById(newsId);
        return newsAssembler.convertToDetailVo(news);
    }

    @PostMapping
    @ApiOperation("Create a news")
    public NewsDetailVO createBy(@RequestBody @Valid NewsParam newsParam,
                                 @RequestParam(value = "autoSave", required = false, defaultValue = "false") Boolean autoSave) {
        return newsService.createBy(newsParam.convertTo(), newsParam.getTagIds(), newsParam.getCategoryIds(), newsParam.getPostMetas(), autoSave);
    }

    @PutMapping("{newsId:\\d+}")
    @ApiOperation("Update a news")
    public NewsDetailVO updateBy(@PathVariable("newsId") Integer newsId, @RequestBody @Valid NewsParam newsParam,
                                 @RequestParam(value = "autoSave", required = false, defaultValue = "false") Boolean autoSave) {
        return new NewsDetailVO();
    }

    @PutMapping("{newsId:\\d+}/{status}")
    @ApiOperation("Update news status")
    public void updateStatusBy(@PathVariable("newsId") Integer newsId, @PathVariable("status") PostStatus status) {

    }

    @PutMapping("{newsId:\\d+}/status/draft/content")
    @ApiOperation("Update draft news")
    public NewsDetailVO updateDraftBy(@PathVariable("newsId") Integer newsId, @RequestBody PostContentParam contentParam) {
        return new NewsDetailVO();
    }

    @DeleteMapping("{newsId:\\d+}")
    @ApiOperation("Delete a news")
    public NewsDetailVO deleteBy(@PathVariable("newsId") Integer newsId) {
        return new NewsDetailVO();
    }

    @GetMapping("preview/{newsId:\\d+}")
    @ApiOperation("Get a news preview")
    public String preview(@PathVariable("newsId") Integer newsId) {
        return StringUtils.EMPTY;
    }

}
