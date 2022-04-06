package run.halo.app.model.dto.cern;

import java.util.Date;

import lombok.Data;
import run.halo.app.model.dto.base.OutputConverter;
import run.halo.app.model.entity.cern.Personnel;

/**
 * Personnel output dto.
 *
 * @author lizc(lizc@fists.cn)
 */
@Data
public class PersonnelDTO implements OutputConverter<PersonnelDTO, Personnel> {
    private Long id;
    private String name;
    private String englishName;
    private String slug;
    private String email;
    private String thumbnail;
    private Date createTime;
    private Date updateTime;
}
