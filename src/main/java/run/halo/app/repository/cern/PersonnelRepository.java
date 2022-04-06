package run.halo.app.repository.cern;

import java.util.Optional;

import io.micrometer.core.lang.NonNull;
import run.halo.app.model.entity.cern.Personnel;
import run.halo.app.repository.base.BaseRepository;

/**
 * Personnel repository.
 *
 * @author lizc(lizc@fists.cn)
 */
public interface PersonnelRepository extends BaseRepository<Personnel, Long> {

    long countByNameOrSlug(@NonNull String name, @NonNull String slug);

    Optional<Personnel> getBySlug(@NonNull String slug);

    Optional<Personnel> getByName(@NonNull String name);
}
