package run.halo.app.controller.admin.api.cern;

import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import run.halo.app.model.dto.cern.paper.PaperListDTO;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.model.params.cern.paper.PaperQuery;
import run.halo.app.model.vo.cern.paper.PaperDetailVO;
import run.halo.app.service.assembler.cern.PaperAssembler;
import run.halo.app.service.cern.PaperService;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * paper admin controller.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@RestController("ApiAdminCernPaperController")
@RequestMapping("/api/admin/cern/papers")
public class PaperController {
    private final PaperService paperService;
    private final PaperAssembler paperAssembler;

    /**
     * constructor of paper controller.
     *
     * @param paperService paper service.
     * @param paperAssembler paper assembler.
     */
    public PaperController(PaperService paperService, PaperAssembler paperAssembler) {
        this.paperService = paperService;
        this.paperAssembler = paperAssembler;
    }

    /**
     * get a page of paper.
     *
     * @param pageable page info
     * @param paperQuery query info
     * @param more more
     * @return paper list dto
     */
    @GetMapping
    @ApiOperation("Gets a page of paper")
    public Page<? extends PaperListDTO> pageBy(@PageableDefault(sort = "createTime", direction = DESC) Pageable pageable, PaperQuery paperQuery,
                                               @RequestParam(value = "more", defaultValue = "true") Boolean more) {
        Page<Paper> pageData = paperService.pageBy(paperQuery, pageable);
        if (more) {
            return paperAssembler.convertToListVO(pageData);
        }
        return paperAssembler.convertToListDTO(pageData);
    }

    /**
     * get a paper detail.
     *
     * @param paperId paper id
     * @return paper detail
     */
    @GetMapping("{id:\\d+}")
    @ApiOperation("Gets a paper")
    public PaperDetailVO getBy(@PathVariable("id") Integer paperId) {
        Paper paper = paperService.getById(paperId);
        return paperAssembler.convertToDetailVO(paper);
    }

    @PostMapping
    @ApiOperation("Create a paper")
    public PaperDetailVO createBy() {
        return null;
    }

    @PutMapping
    @ApiOperation("Update a paper")
    public PaperDetailVO updateBy() {
        return null;
    }


    public PaperDetailVO updateStatusBy() {
        return null;
    }


    public PaperDetailVO updateDraftBy() {
        return null;
    }

    public Paper deletePermanently() {
        return null;
    }

    public List<Paper> deletePermanentlyInBatch() {
        return null;
    }

    public String preview() {
        return null;
    }

}
