package run.halo.app.model.dto.cern.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.dto.cern.CernPostListDTO;
import run.halo.app.model.entity.cern.Notification;
import run.halo.app.model.enums.cern.PostType;

/**
 * notification list dto.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NotificationListDTO extends CernPostListDTO<Notification> {
    @Override
    public PostType getPostType() {
        return PostType.NOTIFICATION;
    }
}
