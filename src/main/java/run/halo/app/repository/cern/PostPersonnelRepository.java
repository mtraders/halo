package run.halo.app.repository.cern;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import run.halo.app.model.entity.cern.PostPersonnel;
import run.halo.app.model.projection.cern.PersonnelPostCountProjection;
import run.halo.app.repository.base.BaseRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * post personnel repository.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
public interface PostPersonnelRepository extends BaseRepository<PostPersonnel, Integer> {
    @Query(value = "select personnel_id, count(post_id) as post_count, `type` as post_type from post_personnel pp inner join posts p "
        + "on p.id=pp.post_id and p.status <> 2 " + "group by pp.personnel_id, p.type", nativeQuery = true)
    @NonNull
    List<PersonnelPostCountProjection> findPostCount();

    /**
     * Finds all post personnel by post id in.
     *
     * @param postIds post id collection must not be null
     * @return a list of post personnel.
     */
    @NonNull
    List<PostPersonnel> findAllByPostIdIn(@NonNull Collection<Integer> postIds);

    /**
     * find all personnel ids of the given post id.
     *
     * @param postId post id.
     * @return personnel set.
     */
    @NonNull
    @Query(value = "select distinct pp.personnel_id from post_personnel pp where pp.post_id = ?1", nativeQuery = true)
    Set<Integer> findAllPersonnelIdsByPostId(@NonNull Integer postId);

    /**
     * Deletes post personnel by post id.
     *
     * @param postId post id must not be null
     * @return a list of post personnel deleted
     */
    @NonNull
    List<PostPersonnel> deleteByPostId(@NonNull Integer postId);

    /**
     * delete post personnel by personnel id.
     *
     * @param personnelId personnel id must not be null.
     * @return a list of post personnel deleted.
     */
    @NonNull
    List<PostPersonnel> deleteByPersonnelId(@NonNull Integer personnelId);

    /**
     * Find all post personnel by post id.
     *
     * @param postId post id must not be null.
     * @return a list of post personnel.
     */
    @NonNull
    List<PostPersonnel> findAllByPostId(@NonNull Integer postId);
}
