package run.halo.app.model.dto.cern.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.dto.cern.CernPostListDTO;
import run.halo.app.model.entity.cern.Notification;

/**
 * notification list dto.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NotificationListDTO extends CernPostListDTO<Notification> {
}
