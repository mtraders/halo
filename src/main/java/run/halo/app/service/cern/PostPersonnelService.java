package run.halo.app.service.cern;

import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import run.halo.app.model.entity.cern.Personnel;
import run.halo.app.model.entity.cern.PostPersonnel;
import run.halo.app.model.vo.cern.personnel.PersonnelListVO;
import run.halo.app.service.base.CrudService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    List<PersonnelListVO> listPersonnelMore(@NonNull Sort sort);

    /**
     * remove post personnel by personnel id.
     *
     * @param personnelId personnel id
     * @return removed post personnel list
     */
    @NonNull
    List<PostPersonnel> removeByPersonnelId(@NonNull Integer personnelId);

    /**
     * Merges or creates post personnel by post id and personnel id set if absent.
     *
     * @param postId post id must not be null
     * @param personnelIds personnel id set
     * @return a list of post personnel
     */
    @NonNull
    List<PostPersonnel> mergeOrCreateByIfAbsent(@NonNull Integer postId, @Nullable Set<Integer> personnelIds);

    /**
     * remove post personnel by post id.
     *
     * @param postId post id
     * @return post personnel.
     */
    @NonNull
    List<PostPersonnel> removeByPostId(@NonNull Integer postId);

    /**
     * List personnel list map by post id collection.
     *
     * @param postIds post id collection.
     * @return a personnel list map (key: postId, value: a list of personnel)
     */
    @NonNull
    Map<Integer, List<Personnel>> listPersonnelListMap(@Nullable Collection<Integer> postIds);

    /**
     * Lists personnel by post id.
     *
     * @param postId post id must not be null
     * @return a list of personnel
     */
    @NonNull
    List<Personnel> listPersonnelListBy(@NonNull Integer postId);
}
