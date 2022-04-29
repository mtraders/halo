package run.halo.app.controller.content.api.cern;

import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import run.halo.app.model.vo.cern.news.NewsDetailVO;
import run.halo.app.model.vo.cern.news.NewsListVO;

/**
 * News content controller.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@RestController("ApiContentCernNewsController")
@RequestMapping(value = "/api/content/cern/news")
public class NewsController {

    @GetMapping
    @ApiOperation("Lists news")
    public Page<NewsListVO> pageBy() {
        return null;
    }


    @PostMapping(value = "search")
    @ApiOperation("Lists news by keyword")
    public Page<NewsListVO> pageBy(String keyword) {
        return null;
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
