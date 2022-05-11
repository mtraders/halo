package run.halo.app.model.dto.cern.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.lang.NonNull;
import run.halo.app.model.dto.cern.CernPostDetailDTO;
import run.halo.app.model.dto.cern.CernPostListDTO;
import run.halo.app.model.entity.Content;
import run.halo.app.model.entity.cern.Notification;
import run.halo.app.model.enums.cern.PostType;

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

    /**
     * Convert from domain.(shallow)
     *
     * @param domain domain data
     * @return converted dto data
     */
    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends CernPostListDTO<Notification>> T convertFrom(@NonNull Notification domain) {
        NotificationDetailDTO detailDTO = super.convertFrom(domain);
        Content.PatchedContent content = domain.getContent();
        detailDTO.setContent(content.getContent());
        detailDTO.setOriginalContent(content.getOriginalContent());
        return (T) detailDTO;
    }
}
