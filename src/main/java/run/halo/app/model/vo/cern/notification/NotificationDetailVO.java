package run.halo.app.model.vo.cern.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.model.dto.CategoryDTO;
import run.halo.app.model.dto.TagDTO;
import run.halo.app.model.dto.cern.notification.NotificationDetailDTO;

import java.util.List;

/**
 * notification detail vo.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationDetailVO extends NotificationDetailDTO {
    private List<TagDTO> tags;
    private List<CategoryDTO> categories;
}
