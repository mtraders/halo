package run.halo.app.service.cern.impl;

import org.springframework.stereotype.Service;
import run.halo.app.model.entity.Content;
import run.halo.app.model.entity.cern.Project;
import run.halo.app.repository.cern.ProjectRepository;
import run.halo.app.service.ContentPatchLogService;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.cern.ProjectService;
import run.halo.app.service.impl.BasePostServiceImpl;

/**
 * project service impl.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Service
public class ProjectServiceImpl extends BasePostServiceImpl<Project> implements ProjectService {

    private final ContentPatchLogService contentPatchLogService;

    public ProjectServiceImpl(ProjectRepository projectRepository, OptionService optionService, ContentService contentService,
                              ContentPatchLogService contentPatchLogService) {
        super(projectRepository, optionService, contentService, contentPatchLogService);
        this.contentPatchLogService = contentPatchLogService;
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
}
