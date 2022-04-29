package run.halo.app.model.dto.cern.personnel;

import lombok.Data;
import run.halo.app.model.dto.base.OutputConverter;
import run.halo.app.model.entity.cern.Personnel;

import java.util.Date;

/**
 * Personnel output dto.
 *
 * @author <a href="mailto:lizc@fists.cn>lizc</a>
 */
@Data
public class PersonnelDTO implements OutputConverter<PersonnelDTO, Personnel> {
    private Integer id;
    private String name;
    private String englishName;
    private String slug;
    private String email;
    private String thumbnail;
    private Date createTime;
    private Date updateTime;
    private String fullPath;
}
