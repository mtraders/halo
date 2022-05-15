package run.halo.app.repository.cern;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import run.halo.app.model.entity.cern.PostUser;
import run.halo.app.repository.base.BaseRepository;

import java.util.List;

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
}
