package run.halo.app.service.cern.impl;

import run.halo.app.model.entity.cern.Personnel;
import run.halo.app.repository.base.BaseRepository;
import run.halo.app.service.base.AbstractCrudService;
import run.halo.app.service.cern.PersonnelService;

/**
 * PersonnelService implements.
 *
 * @author lizc(lizc@fists.cn)
 */
public class PersonnelServiceImpl  extends AbstractCrudService<Personnel, Long> implements PersonnelService {
    protected PersonnelServiceImpl(BaseRepository<Personnel, Long> repository) {
        super(repository);
    }
}
