package run.halo.app.repository.cern;

import java.util.Optional;

import io.micrometer.core.lang.NonNull;
import run.halo.app.model.entity.cern.Personnel;
import run.halo.app.repository.base.BaseRepository;

/**
 * Personnel repository.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 *
 */
public interface PersonnelRepository extends BaseRepository<Personnel, Integer> {

    long countByNameOrSlug(@NonNull String name, @NonNull String slug);

    Optional<Personnel> getBySlug(@NonNull String slug);

    Optional<Personnel> getByName(@NonNull String name);
}
