package run.halo.app.repository.cern;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import run.halo.app.model.entity.cern.PostUser;
import run.halo.app.repository.base.BaseRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * post user repository.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
public interface PostUserRepository extends BaseRepository<PostUser, Integer>, JpaSpecificationExecutor<PostUser> {
    List<PostUser> findByPostIdAndUserId(Integer postId, Integer userId);

    /**
     * delete post users by post id.
     *
     * @param postId post id must not be null
     * @return a list of post user deleted.
     */
    @NonNull
    List<PostUser> deleteByPostId(@NonNull Integer postId);

    /**
     * find all by post id.
     *
     * @param postId post id
     * @return post user list.
     */
    @NonNull
    List<PostUser> findAllByPostId(@NonNull Integer postId);

    /**
     * find all user ids by post id.
     *
     * @param postId post id, not null
     * @return user id set
     */
    @NonNull
    @Query("select postUser.userId from PostUser postUser where postUser.postId = ?1")
    Set<Integer> findAllUserIdsByPostId(@NonNull Integer postId);

    /**
     * Find all post users by post id in.
     *
     * @param postIds post id collection must not be null.
     * @return a list of post user
     */
    @NonNull
    List<PostUser> findAllByPostIdIn(@NonNull Collection<Integer> postIds);
}
