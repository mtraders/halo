package run.halo.app.repository.cern;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.repository.base.BasePostRepository;

/**
 * paper repository.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
public interface PaperRepository extends BasePostRepository<Paper>, JpaSpecificationExecutor<Paper> {
}
