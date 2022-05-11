package run.halo.app.service.cern.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import run.halo.app.model.entity.Content;
import run.halo.app.model.entity.cern.Project;
import run.halo.app.model.params.cern.project.ProjectQuery;
import run.halo.app.repository.cern.ProjectRepository;
import run.halo.app.service.ContentPatchLogService;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.assembler.cern.ProjectAssembler;
import run.halo.app.service.cern.ProjectService;
import run.halo.app.service.impl.BasePostServiceImpl;

/**
 * project service impl.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Service
public class ProjectServiceImpl extends BasePostServiceImpl<Project> implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ContentPatchLogService contentPatchLogService;
    private final ProjectAssembler projectAssembler;

    public ProjectServiceImpl(ProjectRepository projectRepository, OptionService optionService, ContentService contentService,
                              ContentPatchLogService contentPatchLogService, ProjectAssembler projectAssembler) {
        super(projectRepository, optionService, contentService, contentPatchLogService);
        this.projectRepository = projectRepository;
        this.contentPatchLogService = contentPatchLogService;
        this.projectAssembler = projectAssembler;
    }

    /**
     * Get post with the latest content by id.
     * content from patch log.
     *
     * @param postId post id.
     * @return post with the latest content.
     */
    @Override
    public Project getWithLatestContentById(Integer postId) {
        Project project = getById(postId);
        Content content = getContentById(postId);
        Content.PatchedContent patchedContent = contentPatchLogService.getPatchedContentById(content.getHeadPatchLogId());
        project.setContent(patchedContent);
        return project;
    }

    /**
     * Page project.
     *
     * @param projectQuery project query.
     * @param pageable page info.
     * @return project page.
     */
    @Override
    @NonNull
    public Page<Project> pageBy(@NonNull ProjectQuery projectQuery, @NonNull Pageable pageable) {
        Assert.notNull(projectQuery, "Project query must not be null");
        Assert.notNull(pageable, "Project page info must not be null");
        Specification<Project> projectSpecification = projectAssembler.buildSpecByQuery(projectQuery);
        return projectRepository.findAll(projectSpecification, pageable);
    }

}
