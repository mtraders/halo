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
import run.halo.app.exception.NotFoundException;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.params.cern.paper.PaperQuery;
import run.halo.app.model.vo.cern.paper.PaperDetailVO;
import run.halo.app.model.vo.cern.paper.PaperListVO;
import run.halo.app.service.PostService;
import run.halo.app.service.assembler.cern.PaperAssembler;
import run.halo.app.service.cern.PaperService;

import java.util.Set;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * paper content controller.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@RestController("ApiContentCernPaperController")
@RequestMapping("/api/content/cern/papers")
public class PaperController {
    private final PaperService paperService;
    private final PaperAssembler paperAssembler;
    private final PostService postService;

    /**
     * Constructor of paper content controller.
     *
     * @param paperService paper service.
     * @param paperAssembler paper assembler.
     * @param postService post service.
     */
    public PaperController(PaperService paperService, PaperAssembler paperAssembler, PostService postService) {
        this.paperService = paperService;
        this.paperAssembler = paperAssembler;
        this.postService = postService;
    }

    /**
     * Enable paper search published papers with keywords, categoryId, authorId.
     *
     * @param pageable page info.
     * @param keyword keyword
     * @param categoryId category id.
     * @param authorId author id
     * @return paper list.
     */
    @GetMapping
    @ApiOperation("Lists papers")
    public Page<PaperListVO> pageBy(@PageableDefault(sort = {"topPriority", "createTime"}, direction = DESC) Pageable pageable,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                    @RequestParam(value = "authorId", required = false) Integer authorId) {
        PaperQuery paperQuery = new PaperQuery();
        paperQuery.setKeyword(keyword);
        paperQuery.setCategoryId(categoryId);
        paperQuery.setAuthorIds(Set.of(authorId));
        paperQuery.setStatuses(Set.of(PostStatus.PUBLISHED));
        Page<Paper> paperPage = paperService.pageBy(paperQuery, pageable);
        return paperAssembler.convertToListVO(paperPage);
    }

    /**
     * search paper.
     *
     * @param keyword keyword.
     * @param pageable page info.
     * @return paper list vo.
     */
    @PostMapping(value = "search")
    @ApiOperation("List paper by keyword")
    public Page<PaperListVO> pageBy(@RequestParam(value = "keyword") String keyword,
                                    @PageableDefault(sort = "createTime", direction = DESC) Pageable pageable) {
        PaperQuery paperQuery = new PaperQuery();
        paperQuery.setStatuses(Set.of(PostStatus.PUBLISHED));
        paperQuery.setKeyword(keyword);
        Page<Paper> paperPage = paperService.pageBy(paperQuery, pageable);
        return paperAssembler.convertToListVO(paperPage);
    }

    /**
     * get paper detail.
     *
     * @param paperId paper id.
     * @param formatDisabled format disabled or not.
     * @param sourceDisabled source disabled.
     * @return paper detail.
     */
    @GetMapping("{id:\\d+}")
    @ApiOperation("Get a paper")
    public PaperDetailVO getBy(@PathVariable("id") Integer paperId,
                               @RequestParam(value = "formatDisabled", required = false, defaultValue = "true") Boolean formatDisabled,
                               @RequestParam(value = "sourceDisabled", required = false, defaultValue = "false") Boolean sourceDisabled) {
        Paper paper = paperService.getById(paperId);
        PaperDetailVO detailVO = paperAssembler.convertToDetailVO(paper);
        if (formatDisabled) {
            // Clear the format content
            detailVO.setContent(null);
        }
        if (sourceDisabled) {
            // Clear the original content
            detailVO.setOriginalContent(null);
        }
        postService.publishVisitEvent(detailVO.getId());
        return detailVO;
    }

    /**
     * get paper by slug.
     *
     * @param slug slug.
     * @param formatDisabled format disable or not.
     * @param sourceDisabled source disable or not.
     * @return paper detail
     */
    @GetMapping("/slug")
    @ApiOperation("Get a paper by slug")
    public PaperDetailVO getBy(@RequestParam("slug") String slug,
                               @RequestParam(value = "formatDisabled", required = false, defaultValue = "true") Boolean formatDisabled,
                               @RequestParam(value = "sourceDisabled", required = false, defaultValue = "false") Boolean sourceDisabled) {
        Paper paper = paperService.getBySlug(slug);
        PaperDetailVO detailVO = paperAssembler.convertToDetailVO(paper);
        if (formatDisabled) {
            // Clear the format content
            detailVO.setContent(null);
        }
        if (sourceDisabled) {
            // Clear the original content
            detailVO.setOriginalContent(null);
        }
        postService.publishVisitEvent(detailVO.getId());
        return detailVO;
    }

    /**
     * Get prev paper by current post id.
     *
     * @param paperId paper id.
     * @return paper detail
     */
    @GetMapping("{id:\\d+}/prev")
    @ApiOperation("Get prev paper by current post id")
    public PaperDetailVO getPrevPaperBy(@PathVariable("id") Integer paperId) {
        Paper paper = paperService.getById(paperId);
        Paper prevPaper = paperService.getPrevPost(paper).orElseThrow(() -> new NotFoundException("查询不到该论文信息"));
        return paperAssembler.convertToDetailVO(prevPaper);
    }

    /**
     * Get next paper by current post id.
     *
     * @param paperId paper id.
     * @return paper detail.
     */
    @GetMapping("{id:\\d+}/next")
    @ApiOperation("Get next paper by current post id")
    public PaperDetailVO getNextPaperBy(@PathVariable("id") Integer paperId) {
        Paper paper = paperService.getById(paperId);
        Paper nextPaper = paperService.getNextPost(paper).orElseThrow(() -> new NotFoundException("查询不到该论文信息"));
        return paperAssembler.convertToDetailVO(nextPaper);
    }
}
