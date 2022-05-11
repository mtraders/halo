package run.halo.app.model.vo.cern.personnel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.model.dto.cern.personnel.PersonnelDTO;

/**
 * Personnel list vo.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonnelListVO extends PersonnelDTO {
    private Long newsCount;
    private Long paperCount;
    private Long projectCount;
}
