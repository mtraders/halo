package run.halo.app.model.dto.cern.personnel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * personnel with more information.
 *
 * @author <a href="lizc@fists.cn">lizc</a>
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PersonnelMoreDTO extends PersonnelDTO {
    private Long postCount;
    private Long paperCount;
    private Long projectCount;
}
