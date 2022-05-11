package run.halo.app.controller.content.api.cern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import run.halo.app.model.entity.cern.Project;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.params.cern.project.ProjectQuery;
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
}
