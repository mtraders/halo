package run.halo.app.controller.admin.api.cern;

import io.swagger.annotations.ApiOperation;
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
import run.halo.app.model.dto.cern.news.NewsListDTO;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.params.PostContentParam;
import run.halo.app.model.params.PostQuery;
import run.halo.app.model.params.cern.NewsParam;
import run.halo.app.model.vo.cern.news.NewsDetailVO;
import run.halo.app.model.vo.cern.news.NewsListVO;
import run.halo.app.service.assembler.cern.NewsAssembler;
import run.halo.app.service.cern.NewsService;

import javax.validation.Valid;
import java.util.List;

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
    private final NewsAssembler newsAssembler;

    /**
     * news controller constructor.
     *
     * @param newsService news service
     * @param newsAssembler news assembler.
     */
    public NewsController(NewsService newsService, NewsAssembler newsAssembler) {
        this.newsService = newsService;
        this.newsAssembler = newsAssembler;
    }

    /**
     * get news list.
     *
     * @param pageable page info
     * @return news list vo list.
     */
    @GetMapping
    @ApiOperation("List news")
    public Page<? extends NewsListDTO> pageBy(@PageableDefault(sort = {"topPriority", "createTime"}, direction = DESC) Pageable pageable,
                                              PostQuery newsQuery, @RequestParam(value = "more", defaultValue = "true") Boolean more) {
        Page<News> newsPage = newsService.pageBy(newsQuery, pageable);
        if (more) {
            return newsAssembler.convertToListVo(newsPage);
        }
        return newsAssembler.convertToListDTO(newsPage);
    }

    /**
     * get latest news dto.
     *
     * @param top count
     * @return new list dto list.
     */
    @GetMapping("latest")
    @ApiOperation("Get latest news")
    public List<NewsListDTO> pageLatest(@RequestParam(name = "top", defaultValue = "10") int top) {
        Page<News> newsPage = newsService.pageLatest(top);
        return newsAssembler.convertToListDTO(newsPage).getContent();
    }

    @GetMapping("{id:\\d+}")
    @ApiOperation("Get a news")
    public NewsDetailVO getBy(@PathVariable("id") Integer newsId) {
        News news = newsService.getWithLatestContentById(newsId);
        return newsAssembler.convertToDetailVo(news);
    }

    /**
     * Create news.
     *
     * @param newsParam news param.
     * @param autoSave auto save or not.
     * @return news detail vo.
     */
    @PostMapping
    @ApiOperation("Create a news")
    public NewsDetailVO createBy(@RequestBody @Valid NewsParam newsParam,
                                 @RequestParam(value = "autoSave", required = false, defaultValue = "false") Boolean autoSave) {
        return newsService.createBy(newsParam.convertTo(), newsParam.getTagIds(), newsParam.getCategoryIds(), newsParam.getPostMetas(), autoSave);
    }

    /**
     * Update news.
     *
     * @param newsId news id.
     * @param newsParam news param.
     * @param autoSave auto save flag.
     * @return news detail vo.
     */
    @PutMapping("{id:\\d+}")
    @ApiOperation("Update a news")
    public NewsDetailVO updateBy(@PathVariable("id") Integer newsId, @RequestBody @Valid NewsParam newsParam,
                                 @RequestParam(value = "autoSave", required = false, defaultValue = "false") Boolean autoSave) {
        News newsToUpdate = newsService.getWithLatestContentById(newsId);
        newsParam.update(newsToUpdate);
        return newsService.updateBy(newsToUpdate, newsParam.getTagIds(), newsParam.getCategoryIds(), newsParam.getPostMetas(), autoSave);
    }

    /**
     * Update news status.
     *
     * @param newsId news id.
     * @param status status.
     * @return news list vo.
     */
    @PutMapping("{id:\\d+}/{status}")
    @ApiOperation("Update news status")
    public NewsListVO updateStatusBy(@PathVariable("id") Integer newsId, @PathVariable("status") PostStatus status) {
        News news = newsService.updateStatus(status, newsId);
        return new NewsListVO().convertFrom(news);
    }

    /**
     * Update draft news.
     *
     * @param newsId news id.
     * @param contentParam content param.
     * @return news detail vo.
     */
    @PutMapping("{id:\\d+}/status/draft/content")
    @ApiOperation("Update draft news")
    public NewsDetailVO updateDraftBy(@PathVariable("id") Integer newsId, @RequestBody PostContentParam contentParam) {
        News newsToUse = newsService.getById(newsId);
        String formattedContent = contentParam.decideContentBy(newsToUse.getEditorType());
        News news = newsService.updateDraftContent(formattedContent, contentParam.getOriginalContent(), newsId);
        return newsAssembler.convertToDetailVo(news);
    }

    /**
     * delete a news.
     *
     * @param newsId news id.
     * @return news entity.
     */
    @DeleteMapping("{id:\\d+}")
    @ApiOperation("Delete a news")
    public News deletePermanently(@PathVariable("id") Integer newsId) {
        return newsService.removeById(newsId);
    }

    /**
     * deletes news permanently in batch.
     *
     * @param ids ids
     * @return news entities
     */
    @DeleteMapping
    @ApiOperation("Deletes news permanently in batch by id array")
    public List<News> deletePermanentlyInBatch(@RequestBody List<Integer> ids) {
        return newsService.removeByIds(ids);
    }

    /**
     * get a news preview.
     *
     * @param newsId news id.
     * @return preview content.
     */
    @GetMapping("preview/{id:\\d+}")
    @ApiOperation("Get a news preview")
    public String preview(@PathVariable("id") Integer newsId) {
        return newsId.toString();
    }

}
