package run.halo.app.repository.cern;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import run.halo.app.model.entity.cern.News;
import run.halo.app.repository.base.BasePostRepository;

public interface NewsRepository extends BasePostRepository<News>, JpaSpecificationExecutor<News> {
}
