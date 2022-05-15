package run.halo.app.service.cern;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import run.halo.app.model.entity.User;
import run.halo.app.model.entity.cern.PostUser;
import run.halo.app.service.base.CrudService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Post user service.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
public interface PostUserService extends CrudService<PostUser, Integer> {
    /**
     * is post user exists by given post id and user id.
     *
     * @param postId post id
     * @param userId user id
     * @return exists or not
     */
    boolean exists(Integer postId, Integer userId);

    /**
     * remove post users by post id.
     *
     * @param postId post id
     * @return deleted post user
     */
    @NonNull
    List<PostUser> removeByPostId(@NonNull Integer postId);

    /**
     * Merges or creates post users by post id and user id set if absent.
     *
     * @param postId post id
     * @param userIds user id set
     * @return post users
     */
    @NonNull
    List<PostUser> mergeOrCreateByIfAbsent(@NonNull Integer postId, @Nullable Set<Integer> userIds);

    /**
     * Lists user by post id.
     *
     * @param postId post id must not be null
     * @return a list of user
     */
    @NonNull
    List<User> listUsersBy(@NonNull Integer postId);

    /**
     * List user list map by post id collection.
     *
     * @param postIds post id collection
     * @return a user list map (key: postId, value: a list of user)
     */
    Map<Integer, List<User>> listUserListMap(@Nullable Collection<Integer> postIds);
}
