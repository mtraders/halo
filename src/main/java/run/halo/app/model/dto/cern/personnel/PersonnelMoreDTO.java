package run.halo.app.model.dto.cern;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * personnel with more inforamtion.
 *
 * @author lizc(lizc@fists.cn)
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PersonnelMoreDTO extends PersonnelDTO {
    private Long postCount;
    private Long paperCount;
    private Long projectCount;
}
