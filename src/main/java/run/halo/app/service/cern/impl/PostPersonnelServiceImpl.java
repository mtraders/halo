package run.halo.app.service.cern.impl;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import run.halo.app.model.dto.cern.personnel.PersonnelMoreDTO;
import run.halo.app.model.entity.cern.Personnel;
import run.halo.app.model.entity.cern.PostPersonnel;
import run.halo.app.model.enums.cern.PostType;
import run.halo.app.model.projection.cern.PersonnelPostCountProjection;
import run.halo.app.repository.cern.PersonnelRepository;
import run.halo.app.repository.cern.PostPersonnelRepository;
import run.halo.app.service.base.AbstractCrudService;
import run.halo.app.service.cern.PostPersonnelService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * build personnel post count key.
     *
     * @param personnelId personnel id
     * @param postType post type
     * @return key
     */
    private static String buildCountKey(int personnelId, int postType) {
        return StringUtils.joinWith(",", personnelId, postType);
    }

    @Override
    public @NotNull List<PersonnelMoreDTO> listPersonnelMore(@NotNull Sort sort) {
        Assert.notNull(sort, "Sort info must not be null");
        // Find all personnel
        List<Personnel> personnelList = personnelRepository.findAll(sort);
        // Find personnel posts.
        List<PersonnelPostCountProjection> personnelPostCountList = postPersonnelRepository.findPostCount();
        // build count map, key: personnel_id,post_type
        Map<String, PersonnelPostCountProjection> personnelPostCountMap = Maps.uniqueIndex(personnelPostCountList, personnelPostCount -> {
            int personnelId = personnelPostCount.getPersonnelId();
            int postType = personnelPostCount.getPostType();
            return buildCountKey(personnelId, postType);
        });

        return personnelList.stream().map(personnel -> {
            int personnelId = personnel.getId();
            PersonnelMoreDTO personnelMoreDTO = new PersonnelMoreDTO().convertFrom(personnel);
            PersonnelPostCountProjection personnelPostDefaultCount =
                personnelPostCountMap.getOrDefault(buildCountKey(personnelId, PostType.BASE.getValue()), new PersonnelPostCountProjection());
            Assert.notNull(personnelPostDefaultCount, "personnel post count info must not be null");
            personnelMoreDTO.setPostCount(personnelPostDefaultCount.getPostCount());

            PersonnelPostCountProjection personnelPaperCount =
                personnelPostCountMap.getOrDefault(buildCountKey(personnelId, PostType.PAPER.getValue()), new PersonnelPostCountProjection());
            Assert.notNull(personnelPaperCount, "personnel paper count info must not be null");
            personnelMoreDTO.setPaperCount(personnelPaperCount.getPostCount());

            PersonnelPostCountProjection personnelProjectCount =
                personnelPostCountMap.getOrDefault(buildCountKey(personnelId, PostType.PROJECT.getValue()), new PersonnelPostCountProjection());
            Assert.notNull(personnelProjectCount, "personnel project count info must not be null");
            personnelMoreDTO.setProjectCount(personnelProjectCount.getPostCount());

            return personnelMoreDTO;
        }).collect(Collectors.toList());
    }

    /**
     * remove post personnel by personnel id.
     *
     * @param personnelId personnel id
     * @return deleted postpersonnel
     */
    @Override
    public @NotNull List<PostPersonnel> removeByPersonnelId(@NotNull Integer personnelId) {
        return Collections.emptyList();
    }
}
