package run.halo.app.service.cern.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.util.StringUtils;
import org.springframework.util.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import run.halo.app.exception.AlreadyExistsException;
import run.halo.app.model.dto.cern.personnel.PersonnelDTO;
import run.halo.app.model.dto.cern.personnel.PersonnelMoreDTO;
import run.halo.app.model.entity.cern.Personnel;
import run.halo.app.repository.cern.PersonnelRepository;
import run.halo.app.service.base.AbstractCrudService;
import run.halo.app.service.cern.PersonnelService;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PersonnelService implements.
 *
 * @author <a href="mailto:lizc@fists.cn>lizc</a>
 */
@Slf4j
@Service
public class PersonnelServiceImpl extends AbstractCrudService<Personnel, Integer> implements PersonnelService {

    private final PersonnelRepository personnelRepository;

    public PersonnelServiceImpl(PersonnelRepository personnelRepository) {
        super(personnelRepository);
        this.personnelRepository = personnelRepository;
    }

    public @NotNull PersonnelDTO convertTo(@NotNull Personnel personnel) {
        return new PersonnelDTO().convertFrom(personnel);
    }

    @Override
    public @NotNull List<PersonnelDTO> convertTo(@NotNull List<Personnel> personnelList) {
        if (CollectionUtils.isEmpty(personnelList)) {
            return Collections.emptyList();
        }
        return personnelList.stream().map(this::convertTo).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Personnel create(Personnel personnel) {
        long count = personnelRepository.countByNameOrSlug(personnel.getName(), personnel.getSlug());
        log.debug("Personnel count: [{}]", count);
        if (count > 0) {
            String name = personnel.getName();
            String message = StringUtils.format("人员 %s 已存在", name);
            throw new AlreadyExistsException(message).setErrorData(personnel);
        }
        return super.create(personnel);
    }
}
