package run.halo.app.repository.cern;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import run.halo.app.model.entity.cern.Notification;
import run.halo.app.repository.base.BasePostRepository;

/**
 * notification repository.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
public interface NotificationRepository extends BasePostRepository<Notification>, JpaSpecificationExecutor<Notification> {
}
