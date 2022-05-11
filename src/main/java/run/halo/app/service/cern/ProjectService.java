package run.halo.app.service.cern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.model.entity.cern.Project;
import run.halo.app.model.params.cern.paper.PaperQuery;
import run.halo.app.model.params.cern.project.ProjectQuery;
import run.halo.app.service.base.BasePostService;

/**
 * project service.
 *
 * @author <a href="mailto:lizc@fits.cn">lizc</a>
 */
public interface ProjectService extends BasePostService<Project> {

    /**
     * Page project.
     *
     * @param projectQuery project query.
     * @param pageable page info.
     * @return project page.
     */
    @NonNull
    Page<Project> pageBy(@NonNull ProjectQuery projectQuery, @NonNull Pageable pageable);
}
