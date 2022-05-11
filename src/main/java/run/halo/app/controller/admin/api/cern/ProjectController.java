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
import run.halo.app.model.dto.cern.project.ProjectListDTO;
import run.halo.app.model.entity.cern.Project;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.params.PostContentParam;
import run.halo.app.model.params.cern.project.ProjectParam;
import run.halo.app.model.params.cern.project.ProjectQuery;
import run.halo.app.model.vo.cern.project.ProjectDetailVO;
import run.halo.app.model.vo.cern.project.ProjectListVO;
import run.halo.app.service.assembler.cern.ProjectAssembler;
import run.halo.app.service.cern.ProjectService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * project admin controller.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@RestController("ApiAdminCernProjectController")
@RequestMapping("/api/admin/cern/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectAssembler projectAssembler;

    public ProjectController(ProjectService projectService, ProjectAssembler projectAssembler) {
        this.projectService = projectService;
        this.projectAssembler = projectAssembler;
    }

    /**
     * get project list.
     *
     * @param pageable page info.
     * @param projectQuery project query info.
     * @param more more info or not
     * @return project list data.
     */
    @GetMapping
    @ApiOperation(value = "get project list")
    public Page<? extends ProjectListDTO> pageBy(@PageableDefault(sort = {"topPriority", "createTime"}, direction = DESC) Pageable pageable,
                                                 ProjectQuery projectQuery, @RequestParam(value = "more", defaultValue = "true") Boolean more) {
        Page<Project> pageData = projectService.pageBy(projectQuery, pageable);
        if (more) {
            return projectAssembler.convertToListVO(pageData);
        }
        return projectAssembler.convertToListDTO(pageData);
    }

    /**
     * get a project detail by id.
     *
     * @param projectId project id.
     * @return project detail.
     */
    @GetMapping("{id:\\d+}")
    @ApiOperation("Get a project")
    public ProjectDetailVO getBy(@PathVariable("id") Integer projectId) {
        Project project = projectService.getById(projectId);
        return projectAssembler.convertToDetailVO(project);
    }

    /**
     * create a project.
     *
     * @param projectParam project param
     * @param autoSave auto-save flag.
     * @return paper detail vo.
     */
    @PostMapping
    @ApiOperation("create a project")
    public ProjectDetailVO createBy(@RequestBody @Valid ProjectParam projectParam,
                                    @RequestParam(value = "autoSave", required = false, defaultValue = "false") Boolean autoSave) {
        Project projectToCreate = projectParam.convertTo();
        Set<Integer> tagIds = projectParam.getTagIds();
        Set<Integer> categoryIds = projectParam.getCategoryIds();
        Set<Integer> managerIds = projectParam.getManagerIds();
        return projectService.createBy(projectToCreate, tagIds, categoryIds, managerIds, autoSave);
    }

    /**
     * update a project by id.
     *
     * @param projectId project id.
     * @param projectParam project param.
     * @param autoSave auto-save or not.
     * @return project detail vo.
     */
    @PutMapping("{id:\\d+}")
    @ApiOperation("update a project")
    public ProjectDetailVO updateBy(@PathVariable("id") Integer projectId, @RequestBody @Valid ProjectParam projectParam,
                                    @RequestParam(value = "autoSave", required = false, defaultValue = "false") Boolean autoSave) {
        Project projectToUpdate = projectService.getWithLatestContentById(projectId);
        projectParam.update(projectToUpdate);

        Set<Integer> tagIds = projectParam.getTagIds();
        Set<Integer> categoryIds = projectParam.getCategoryIds();
        Set<Integer> managerIds = projectParam.getManagerIds();

        return projectService.updateBy(projectToUpdate, tagIds, categoryIds, managerIds, autoSave);
    }

    /**
     * update project status.
     *
     * @param projectId project id.
     * @param status status.
     * @return project list vo.
     */
    @PutMapping("{id:\\d+}/{status}")
    @ApiOperation("update project status")
    public ProjectListVO updateStatusBy(@PathVariable("id") Integer projectId, @PathVariable("status") PostStatus status) {
        return projectAssembler.convertToListVO(projectService.updateStatus(status, projectId));
    }

    /**
     * update draft project.
     *
     * @param projectId project id.
     * @param contentParam content param.
     * @return project detail vo.
     */
    @PutMapping("{id:\\d+}/status/draft/content")
    @ApiOperation("update draft project")
    public ProjectDetailVO updateDraftBy(@PathVariable("id") Integer projectId, @RequestBody PostContentParam contentParam) {
        Project projectToUse = projectService.getById(projectId);
        String formattedContent = contentParam.decideContentBy(projectToUse.getEditorType());
        Project project = projectService.updateDraftContent(formattedContent, contentParam.getOriginalContent(), projectId);
        return projectAssembler.convertToDetailVO(project);
    }

    /**
     * Delete a project permanently.
     *
     * @param projectId project id.
     * @return deleted project
     */
    @DeleteMapping("{id:\\d+}")
    @ApiOperation("Delete a project permanently.")
    public Project deletePermanently(@PathVariable("id") Integer projectId) {
        return projectService.removeById(projectId);
    }

    @DeleteMapping
    @ApiOperation("Deletes projects permanently in batch by id array")
    public List<Project> deletePermanentlyInBatch(@RequestBody List<Integer> ids) {
        return projectService.removeByIds(ids);
    }

    /**
     * get a project preview.
     *
     * @param projectId project id.
     * @return preview content.
     */
    @GetMapping("preview/{id:\\d+}")
    @ApiOperation("Get a project preview")
    public String preview(@PathVariable("id") Integer projectId) {
        return projectId.toString();
    }
}
