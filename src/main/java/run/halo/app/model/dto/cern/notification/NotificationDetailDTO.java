package run.halo.app.model.dto.cern.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.lang.NonNull;
import run.halo.app.model.entity.Content;
import run.halo.app.model.entity.cern.Notification;

/**
 * notification detail dto.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NotificationDetailDTO extends NotificationListDTO {
    private String originalContent;
    private String content;
    private Long commentCount;

    /**
     * convert notification entity to notification detail dto.
     *
     * @param notification notification entity.
     * @param <T> notification detail dto.
     * @return notificattion dto
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends NotificationListDTO> T convertFrom(@NonNull Notification notification) {
        NotificationDetailDTO notificationDetailDTO = super.convertFrom(notification);
        Content.PatchedContent content = notification.getContent();
        notificationDetailDTO.setContent(content.getContent());
        notificationDetailDTO.setOriginalContent(content.getOriginalContent());
        return (T) notificationDetailDTO;
    }
}
