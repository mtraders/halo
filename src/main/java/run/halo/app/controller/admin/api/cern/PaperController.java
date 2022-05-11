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
import run.halo.app.model.dto.cern.paper.PaperListDTO;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.params.PostContentParam;
import run.halo.app.model.params.cern.paper.PaperParam;
import run.halo.app.model.params.cern.paper.PaperQuery;
import run.halo.app.model.vo.cern.paper.PaperDetailVO;
import run.halo.app.model.vo.cern.paper.PaperListVO;
import run.halo.app.service.assembler.cern.PaperAssembler;
import run.halo.app.service.cern.PaperService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

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

    /**
     * create a paper.
     *
     * @param paperParam paper param
     * @param autoSave auto-save
     * @return paper detail vo.
     */
    @PostMapping
    @ApiOperation("Create a paper")
    public PaperDetailVO createBy(@RequestBody @Valid PaperParam paperParam,
                                  @RequestParam(value = "autoSave", required = false, defaultValue = "false") Boolean autoSave) {
        Paper paperToCreate = paperParam.convertTo();
        Set<Integer> tagIds = paperParam.getTagIds();
        Set<Integer> categoryIds = paperParam.getCategoryIds();
        Set<Integer> authorIds = paperParam.getAuthorIds();
        return paperService.createBy(paperToCreate, tagIds, categoryIds, authorIds, autoSave);
    }

    /**
     * Update paper.
     *
     * @param paperId paper id
     * @param paperParam paper param
     * @param autoSave auto-save
     * @return paper detail.
     */
    @PutMapping("{id:\\d+}")
    @ApiOperation("Update a paper")
    public PaperDetailVO updateBy(@PathVariable("id") Integer paperId, @RequestBody @Valid PaperParam paperParam,
                                  @RequestParam(value = "autoSave", required = false, defaultValue = "false") Boolean autoSave) {
        Paper paperToUpdate = paperService.getWithLatestContentById(paperId);
        paperParam.update(paperToUpdate);
        Set<Integer> tagIds = paperParam.getTagIds();
        Set<Integer> categoryIds = paperParam.getCategoryIds();
        Set<Integer> authorIds = paperParam.getAuthorIds();
        return paperService.updateBy(paperToUpdate, tagIds, categoryIds, authorIds, autoSave);
    }


    /**
     * Update paper status.
     *
     * @param paperId paper id.
     * @param status status.
     * @return paper list vo.
     */
    @PutMapping("{id:\\d+}/{status}")
    @ApiOperation("Update paper status")
    public PaperListVO updateStatusBy(@PathVariable("id") Integer paperId, @PathVariable("status") PostStatus status) {
        return paperAssembler.convertToListVO(paperService.updateStatus(status, paperId));
    }

    /**
     * Update draft paper.
     *
     * @param paperId paper id
     * @param contentParam content param
     * @return paper detail vo.
     */
    @PutMapping("{id:\\d+}/status/draft/content")
    @ApiOperation("Update draft paper")
    public PaperDetailVO updateDraftBy(@PathVariable("id") Integer paperId, @RequestBody PostContentParam contentParam) {
        Paper paperToUse = paperService.getById(paperId);
        String formattedContent = contentParam.decideContentBy(paperToUse.getEditorType());
        Paper paper = paperService.updateDraftContent(formattedContent, contentParam.getOriginalContent(), paperId);
        return paperAssembler.convertToDetailVO(paper);
    }

    /**
     * Delete a paper permanently.
     *
     * @param paperId paper id
     * @return deleted paper entity.
     */
    @DeleteMapping("{id:\\d+}")
    @ApiOperation("Delete a paper")
    public Paper deletePermanently(@PathVariable("id") Integer paperId) {
        return paperService.removeById(paperId);
    }

    /**
     * delete paper permanently in batch.
     *
     * @param ids paper ids.
     * @return deleted paper entities.
     */
    @DeleteMapping
    @ApiOperation("Deletes paper permanently in batch by id array")
    public List<Paper> deletePermanentlyInBatch(@RequestBody List<Integer> ids) {
        return paperService.removeByIds(ids);
    }

    /**
     * get a paper preview.
     *
     * @param paperId paper id.
     * @return preview content.
     */
    @GetMapping("preview/{id:\\d+}")
    @ApiOperation("Get a paper preview")
    public String preview(@PathVariable("id") Integer paperId) {
        return paperId.toString();
    }

}
