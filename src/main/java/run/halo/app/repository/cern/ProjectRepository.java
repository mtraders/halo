package run.halo.app.repository.cern;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import run.halo.app.model.entity.cern.Project;
import run.halo.app.repository.base.BasePostRepository;

/**
 * project repository.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
public interface ProjectRepository extends BasePostRepository<Project>, JpaSpecificationExecutor<Project> {
}
