package run.halo.app.repository.cern;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import run.halo.app.model.entity.cern.News;
import run.halo.app.repository.base.BasePostRepository;

/**
 * news repository.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
public interface NewsRepository extends BasePostRepository<News>, JpaSpecificationExecutor<News> {
}
