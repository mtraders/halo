package run.halo.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;

import run.halo.app.model.entity.User;
import run.halo.app.repository.base.BaseRepository;

/**
 * User repository.
 *
 * @author johnniang
 */
public interface UserRepository extends BaseRepository<User, Integer>, JpaSpecificationExecutor<User> {

    /**
     * Gets user by username.
     *
     * @param username username must not be blank
     * @return an optional user
     */
    @NonNull
    Optional<User> findByUsername(@NonNull String username);

    /**
     * Gets user by email.
     *
     * @param email email must not be blank
     * @return an optional user
     */
    @NonNull
    Optional<User> findByEmail(@NonNull String email);
}
