package run.halo.app.service.cern;

import org.springframework.lang.NonNull;
import run.halo.app.model.dto.cern.personnel.PersonnelDTO;
import run.halo.app.model.entity.cern.Personnel;
import run.halo.app.service.base.CrudService;

import java.util.List;

/**
 * Cern personnel service interface.
 *
 * @author lizc mailto:lizc@fists.cn
 */
public interface PersonnelService extends CrudService<Personnel, Integer> {
    @NonNull
    List<PersonnelDTO> convertTo(@NonNull List<Personnel> personnelList);
}
