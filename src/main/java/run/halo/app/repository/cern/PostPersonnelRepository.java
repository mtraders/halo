package run.halo.app.repository.cern;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import run.halo.app.model.entity.cern.PostPersonnel;
import run.halo.app.model.projection.cern.PersonnelPostCountProjection;
import run.halo.app.repository.base.BaseRepository;

import java.util.List;

/**
 * post personnel repository.
 *
 * @author lizc <a href=mailto:lizc@fists.cn></a>
 */
public interface PostPersonnelRepository extends BaseRepository<PostPersonnel, Integer> {
    @Query(value = "select personnel_id, count(post_id) as post_count, `type` as post_type from post_personnel pp inner join posts p "
        + "on p.id=pp.post_id and p.status <> 2 "
        + "group by pp.personnel_id, p.type", nativeQuery = true)
    @NonNull
    List<PersonnelPostCountProjection> findPostCount();
}
