package run.halo.app.service.cern.impl;

import org.springframework.stereotype.Service;
import run.halo.app.model.entity.cern.PostPersonnel;
import run.halo.app.repository.cern.PersonnelRepository;
import run.halo.app.repository.cern.PostPersonnelRepository;
import run.halo.app.service.base.AbstractCrudService;
import run.halo.app.service.cern.PostPersonnelService;

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
}
