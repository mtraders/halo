package run.halo.app.service.cern.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import run.halo.app.model.entity.cern.Personnel;
import run.halo.app.model.entity.cern.PostPersonnel;
import run.halo.app.model.enums.cern.PostType;
import run.halo.app.model.projection.cern.PersonnelPostCountProjection;
import run.halo.app.model.vo.cern.personnel.PersonnelListVO;
import run.halo.app.repository.cern.PersonnelRepository;
import run.halo.app.repository.cern.PostPersonnelRepository;
import run.halo.app.service.base.AbstractCrudService;
import run.halo.app.service.cern.PersonnelService;
import run.halo.app.service.cern.PostPersonnelService;
import run.halo.app.utils.ServiceUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final PersonnelService personnelService;

    /**
     * post personnel service impl constructor.
     *
     * @param postPersonnelRepository post personnel repository
     * @param personnelRepository personnel repository
     * @param personnelService personnel service.
     */
    public PostPersonnelServiceImpl(PostPersonnelRepository postPersonnelRepository, PersonnelRepository personnelRepository,
                                    PersonnelService personnelService) {
        super(postPersonnelRepository);
        this.postPersonnelRepository = postPersonnelRepository;
        this.personnelRepository = personnelRepository;
        this.personnelService = personnelService;
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
    public @NotNull List<PersonnelListVO> listPersonnelMore(@NotNull Sort sort) {
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
            PersonnelListVO personnelMoreDTO = new PersonnelListVO().convertFrom(personnel);
            PersonnelPostCountProjection personnelPostDefaultCount =
                personnelPostCountMap.getOrDefault(buildCountKey(personnelId, PostType.NEWS.getValue()), new PersonnelPostCountProjection());
            Assert.notNull(personnelPostDefaultCount, "personnel news count info must not be null");
            personnelMoreDTO.setNewsCount(personnelPostDefaultCount.getPostCount());

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
     * @return deleted post personnel
     */
    @Override
    @NonNull
    public List<PostPersonnel> removeByPersonnelId(@NonNull Integer personnelId) {
        Assert.notNull(personnelId, "personnel id must not be null");
        return postPersonnelRepository.deleteByPersonnelId(personnelId);
    }

    /**
     * Merges or creates post personnel by post id and personnel id set if absent.
     *
     * @param postId post id must not be null
     * @param personnelIds personnel id set
     * @return a list of post personnel
     */
    @Override
    @NonNull
    public List<PostPersonnel> mergeOrCreateByIfAbsent(@NonNull Integer postId, Set<Integer> personnelIds) {
        Assert.notNull(postId, "Post id must not be null");
        if (CollectionUtils.isEmpty(personnelIds)) {
            return Collections.emptyList();
        }
        // Create post personnel
        List<PostPersonnel> postPersonnelListStaging = personnelIds.stream().map(personnelId -> {
            // Build post personnel
            PostPersonnel postPersonnel = new PostPersonnel();
            postPersonnel.setPostId(postId);
            postPersonnel.setPersonnelId(personnelId);
            return postPersonnel;
        }).collect(Collectors.toList());

        List<PostPersonnel> postPersonnelListToRemove = Lists.newLinkedList();
        List<PostPersonnel> postPersonnelListToCreate = Lists.newLinkedList();

        List<PostPersonnel> postPersonnelList = postPersonnelRepository.findAllByPostId(postId);
        postPersonnelList.forEach(postPersonnel -> {
            if (!postPersonnelListStaging.contains(postPersonnel)) {
                postPersonnelListToRemove.add(postPersonnel);
            }
        });
        postPersonnelListStaging.forEach(postPersonnelStaging -> {
            if (!postPersonnelList.contains(postPersonnelStaging)) {
                postPersonnelListToCreate.add(postPersonnelStaging);
            }
        });

        // Remove post personnel
        removeAll(postPersonnelListToRemove);
        // Remove all post personnel need to remove
        postPersonnelList.removeAll(postPersonnelListToRemove);

        // Add all created post personnel
        postPersonnelList.addAll(createInBatch(postPersonnelListToCreate));

        // Return post personnel
        return postPersonnelList;
    }

    /**
     * remove post personnel by post id.
     *
     * @param postId post id
     * @return post personnel.
     */
    @Override
    @NonNull
    public List<PostPersonnel> removeByPostId(@NonNull Integer postId) {
        Assert.notNull(postId, "post id must not be null");
        return postPersonnelRepository.deleteByPostId(postId);
    }

    /**
     * List personnel list map by post id collection.
     *
     * @param postIds post id collection.
     * @return a personnel list map (key: postId, value: a list of personnel)
     */
    @Override
    @NonNull
    public Map<Integer, List<Personnel>> listPersonnelListMap(@Nullable Collection<Integer> postIds) {
        if (CollectionUtils.isEmpty(postIds)) {
            return Collections.emptyMap();
        }
        // Find all post personnel
        List<PostPersonnel> postPersonnelList = postPersonnelRepository.findAllByPostIdIn(postIds);
        // Find personnel ids
        Set<Integer> personnelIds = ServiceUtils.fetchProperty(postPersonnelList, PostPersonnel::getPersonnelId);
        // Find all personnel
        List<Personnel> personnelList = personnelService.listAllByIds(personnelIds);
        // Convert to personnel map
        Map<Integer, Personnel> personnelMap = ServiceUtils.convertToMap(personnelList, Personnel::getId);
        // Create personnel list map
        Map<Integer, List<Personnel>> personnelListMap = Maps.newHashMap();
        postPersonnelList.forEach(postCategory -> personnelListMap.computeIfAbsent(postCategory.getPostId(), postId -> Lists.newLinkedList())
            .add(personnelMap.get(postCategory.getPersonnelId())));
        return personnelListMap;
    }

    /**
     * Lists personnel by post id.
     *
     * @param postId post id must not be null
     * @return a list of personnel
     */
    @Override
    @NonNull
    public List<Personnel> listPersonnelListBy(@NonNull Integer postId) {
        Assert.notNull(postId, "post id must not be null");
        Set<Integer> personnelIds = postPersonnelRepository.findAllPersonnelIdsByPostId(postId);
        return personnelService.listAllByIds(personnelIds);
    }
}
