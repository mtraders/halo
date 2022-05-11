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
import run.halo.app.model.entity.cern.Project;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.params.cern.project.ProjectQuery;
import run.halo.app.model.vo.cern.project.ProjectDetailVO;
import run.halo.app.model.vo.cern.project.ProjectListVO;
import run.halo.app.service.PostService;
import run.halo.app.service.assembler.cern.ProjectAssembler;
import run.halo.app.service.cern.ProjectService;

import java.util.Set;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Project content controller.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@RestController("ApiContentCernProjectController")
@RequestMapping("/api/content/cern/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectAssembler projectAssembler;
    private final PostService postService;

    /**
     * Constructor of project content controller.
     *
     * @param projectService project service.
     * @param projectAssembler project assembler.
     * @param postService post service.
     */
    public ProjectController(ProjectService projectService, ProjectAssembler projectAssembler, PostService postService) {
        this.projectService = projectService;
        this.projectAssembler = projectAssembler;
        this.postService = postService;
    }

    /**
     * get project list by keywords, category id, manager id.
     *
     * @param pageable pageable
     * @param keyword keyword
     * @param categoryId category id
     * @param managerId manager id
     * @return project list
     */
    public Page<ProjectListVO> pageBy(@PageableDefault(sort = {"topPriority", "createTime"}, direction = DESC) Pageable pageable,
                                      @RequestParam(value = "keyword", required = false) String keyword,
                                      @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                      @RequestParam(value = "managerId", required = false) Integer managerId) {
        ProjectQuery projectQuery = new ProjectQuery();
        projectQuery.setKeyword(keyword);
        projectQuery.setCategoryId(categoryId);
        projectQuery.setManagerIds(Set.of(managerId));
        projectQuery.setStatuses(Set.of(PostStatus.PUBLISHED));
        Page<Project> projectPage = projectService.pageBy(projectQuery, pageable);
        return projectAssembler.convertToListVO(projectPage);
    }

    /**
     * search project.
     *
     * @param keyword keyword.
     * @param pageable page info.
     * @return project list vo.
     */
    @PostMapping(value = "search")
    @ApiOperation("List project by keyword")
    public Page<ProjectListVO> pageBy(@RequestParam(value = "keyword") String keyword,
                                      @PageableDefault(sort = "createTime", direction = DESC) Pageable pageable) {
        ProjectQuery projectQuery = new ProjectQuery();
        projectQuery.setStatuses(Set.of(PostStatus.PUBLISHED));
        projectQuery.setKeyword(keyword);
        Page<Project> paperPage = projectService.pageBy(projectQuery, pageable);
        return projectAssembler.convertToListVO(paperPage);
    }

    /**
     * get project detail.
     *
     * @param projectId project id.
     * @param formatDisabled format disabled or not.
     * @param sourceDisabled source disabled.
     * @return project detail.
     */
    @GetMapping("{id:\\d+}")
    @ApiOperation("Get a project")
    public ProjectDetailVO getBy(@PathVariable("id") Integer projectId,
                                 @RequestParam(value = "formatDisabled", required = false, defaultValue = "true") Boolean formatDisabled,
                                 @RequestParam(value = "sourceDisabled", required = false, defaultValue = "false") Boolean sourceDisabled) {
        Project project = projectService.getById(projectId);
        ProjectDetailVO detailVO = projectAssembler.convertToDetailVO(project);
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
     * get project by slug.
     *
     * @param slug slug.
     * @param formatDisabled format disable or not.
     * @param sourceDisabled source disable or not.
     * @return project detail
     */
    @GetMapping("/slug")
    @ApiOperation("Get a project by slug")
    public ProjectDetailVO getBy(@RequestParam("slug") String slug,
                                 @RequestParam(value = "formatDisabled", required = false, defaultValue = "true") Boolean formatDisabled,
                                 @RequestParam(value = "sourceDisabled", required = false, defaultValue = "false") Boolean sourceDisabled) {
        Project project = projectService.getBySlug(slug);
        ProjectDetailVO detailVO = projectAssembler.convertToDetailVO(project);
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
     * Get prev project by current post id.
     *
     * @param projectId project id.
     * @return project detail
     */
    @GetMapping("{id:\\d+}/prev")
    @ApiOperation("Get prev project by current post id")
    public ProjectDetailVO getPrevPaperBy(@PathVariable("id") Integer projectId) {
        Project project = projectService.getById(projectId);
        Project prevProject = projectService.getPrevPost(project).orElseThrow(() -> new NotFoundException("查询不到该项目信息"));
        return projectAssembler.convertToDetailVO(prevProject);
    }

    /**
     * Get next project by current post id.
     *
     * @param projectId project id.
     * @return project detail.
     */
    @GetMapping("{id:\\d+}/next")
    @ApiOperation("Get next project by current post id")
    public ProjectDetailVO getNextPaperBy(@PathVariable("id") Integer projectId) {
        Project project = projectService.getById(projectId);
        Project nextProject = projectService.getNextPost(project).orElseThrow(() -> new NotFoundException("查询不到该项目信息"));
        return projectAssembler.convertToDetailVO(nextProject);
    }
}
