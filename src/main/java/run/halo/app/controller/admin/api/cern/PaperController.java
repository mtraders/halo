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
import org.springframework.web.bind.annotation.RestController;
import run.halo.app.model.vo.cern.paper.PaperDetailVO;
import run.halo.app.model.vo.cern.paper.PaperListVO;
import run.halo.app.service.assembler.cern.PaperAssembler;
import run.halo.app.service.cern.PaperService;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController("ApiAdminCernPaperController")
@RequestMapping("/api/admin/cern/papers")
public class PaperController {
    private final PaperService paperService;
    private final PaperAssembler paperAssembler;

    public PaperController(PaperService paperService, PaperAssembler paperAssembler) {
        this.paperService = paperService;
        this.paperAssembler = paperAssembler;
    }

    @GetMapping
    @ApiOperation("Gets a page of paper")
    public Page<PaperListVO> pageBy(@PageableDefault(sort = "createTime", direction = DESC) Pageable pageable) {
        return null;
    }

    @GetMapping("{paperId:\\d+}")
    @ApiOperation("Gets a paper")
    public PaperDetailVO getBy(@PathVariable("paperId") Integer paperId) {
        return null;
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

    public PaperDetailVO deleteBy() {
        return null;
    }


    public String preview() {
        return null;
    }

}
