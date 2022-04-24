package run.halo.app.service.cern.impl;

import org.springframework.stereotype.Service;
import run.halo.app.model.entity.cern.Personnel;
import run.halo.app.repository.cern.PersonnelRepository;
import run.halo.app.service.base.AbstractCrudService;
import run.halo.app.service.cern.PersonnelService;

/**
 * PersonnelService implements.
 *
 * @author <a href="mailto:lizc@fists.cn>lizc</a>
 */
@Service
public class PersonnelServiceImpl extends AbstractCrudService<Personnel, Integer> implements PersonnelService {

    private final PersonnelRepository personnelRepository;

    public PersonnelServiceImpl(PersonnelRepository personnelRepository) {
        super(personnelRepository);
        this.personnelRepository = personnelRepository;
    }
}
