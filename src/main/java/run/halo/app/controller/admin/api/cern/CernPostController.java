package run.halo.app.controller.admin.api.cern;

import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import run.halo.app.model.dto.cern.CernPostListDTO;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.enums.cern.PostType;
import run.halo.app.model.params.cern.CernPostQuery;
import run.halo.app.service.assembler.cern.NewsAssembler;
import run.halo.app.service.cern.NewsService;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Cern post admin controller.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@RestController
@RequestMapping("/api/admin/cern/post")
public class CernPostController {
    private final NewsService newsService;
    private final NewsAssembler newsAssembler;

    public CernPostController(NewsService newsService, NewsAssembler newsAssembler) {
        this.newsService = newsService;
        this.newsAssembler = newsAssembler;
    }

    /**
     * List cern posts.
     *
     * @return cern post list dto.
     */
    @GetMapping
    @ApiOperation("List cern posts")
    public Page<? extends CernPostListDTO<?>> pageBy(@PageableDefault(sort = {"topPriority", "createTime"}, direction = DESC) Pageable pageable,
                                                     CernPostQuery postQuery, @RequestParam(value = "more", defaultValue = "true") Boolean more) {

        Page<News> newsPage = newsService.pageBy(postQuery, pageable);
        if (more) {
            return newsAssembler.convertToListVo(newsPage);
        }
        return newsAssembler.convertToListDTO(newsPage);
    }
}
