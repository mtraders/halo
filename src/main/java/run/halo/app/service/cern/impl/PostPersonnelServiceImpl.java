package run.halo.app.service.cern.impl;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import run.halo.app.model.dto.cern.personnel.PersonnelMoreDTO;
import run.halo.app.model.entity.cern.Personnel;
import run.halo.app.model.entity.cern.PostPersonnel;
import run.halo.app.repository.cern.PersonnelRepository;
import run.halo.app.repository.cern.PostPersonnelRepository;
import run.halo.app.service.base.AbstractCrudService;
import run.halo.app.service.cern.PostPersonnelService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Post personnel service implementation.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Service
public class PostPersonnelServiceImpl extends AbstractCrudService<PostPersonnel, Integer> implements PostPersonnelService {
    private final PostPersonnelRepository postPersonnelRepository;
    private final PersonnelRepository personnelRepository;

    /**
     * post personnel service impl constructor.
     *
     * @param postPersonnelRepository post personnel repository
     * @param personnelRepository personnel repository
     */
    public PostPersonnelServiceImpl(PostPersonnelRepository postPersonnelRepository, PersonnelRepository personnelRepository) {
        super(postPersonnelRepository);
        this.postPersonnelRepository = postPersonnelRepository;
        this.personnelRepository = personnelRepository;
    }

    @Override
    public @NotNull List<PersonnelMoreDTO> listPersonnelMore(@NotNull Sort sort) {
        Assert.notNull(sort, "Sort info must not be null");
        // Find all personnel
        List<Personnel> personnelList = personnelRepository.findAll(sort);
        // Find personnel posts.
        return Collections.emptyList();
    }
}
