package run.halo.app.service.cern;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
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
    List<PersonnelDTO> convertTo(@Nullable List<Personnel> personnelList);

    @NonNull
    PersonnelDTO convertTo(@NonNull Personnel personnel);
}
