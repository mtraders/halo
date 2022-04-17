package run.halo.app.controller.admin.api.cern;

import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.service.cern.PaperService;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/admin/cern/papers")
public class PaperController {
    private final PaperService paperService;

    public PaperController(PaperService paperService) {
        this.paperService = paperService;
    }

    @GetMapping
    @ApiOperation("Gets a page of paper")
    public Page<Paper> pageBy(@PageableDefault(sort = "createTime", direction = DESC) Pageable pageable) {
        return paperService.pageBy(pageable);
    }
}
