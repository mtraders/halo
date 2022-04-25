package run.halo.app.service.cern;

import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import run.halo.app.model.dto.cern.personnel.PersonnelMoreDTO;
import run.halo.app.model.entity.cern.PostPersonnel;
import run.halo.app.service.base.CrudService;

import java.util.List;

/**
 * Post personnel service.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
public interface PostPersonnelService extends CrudService<PostPersonnel, Integer> {
    /**
     * list personnel with more information.
     *
     * @param sort spring sort info, not null.
     * @return personnel more information.
     */
    @NonNull
    List<PersonnelMoreDTO> listPersonnelMore(@NonNull Sort sort);

    @NonNull
    List<PostPersonnel> removeByPersonnelId(@NonNull Integer personnelId);
}
