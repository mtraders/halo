package run.halo.app.service.cern.impl;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import run.halo.app.model.entity.cern.PostUser;
import run.halo.app.repository.cern.PostUserRepository;
import run.halo.app.service.base.AbstractCrudService;
import run.halo.app.service.cern.PostUserService;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Post user service impl.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Service
public class PostUserServiceImpl extends AbstractCrudService<PostUser, Integer> implements PostUserService {
    private final PostUserRepository postUserRepository;

    public PostUserServiceImpl(PostUserRepository postUserRepository) {
        super(postUserRepository);
        this.postUserRepository = postUserRepository;
    }

    @Override
    public boolean exists(Integer postId, Integer userId) {
        List<PostUser> postUsers = postUserRepository.findByPostIdAndUserId(postId, userId);
        return postUsers != null && postUsers.size() > 0;
    }

    @Override
    @NonNull
    @Transactional
    public List<PostUser> removeByPostId(@NonNull Integer postId) {
        Assert.notNull(postId, "postId cannot be null");
        return postUserRepository.deleteByPostId(postId);
    }

    @Override
    @NonNull
    @Transactional
    public List<PostUser> mergeOrCreateByIfAbsent(@NonNull Integer postId, Set<Integer> userIds) {
        Assert.notNull(postId, "postId cannot be null");
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        List<PostUser> postUsersStaging = userIds.stream().map(userId -> {
            // Build post user
            PostUser postUser = new PostUser();
            postUser.setPostId(postId);
            postUser.setUserId(userId);
            return postUser;
        }).collect(Collectors.toList());

        List<PostUser> postUsersToRemove = new LinkedList<>();
        List<PostUser> postUsersToCreate = new LinkedList<>();

        List<PostUser> postUsers = postUserRepository.findAllByPostId(postId);
        postUsers.forEach(postUser -> {
            if (!postUsersStaging.contains(postUser)) {
                postUsersToRemove.add(postUser);
            }
        });
        postUsersStaging.forEach(postUserStaging -> {
            if (!postUsers.contains(postUserStaging)) {
                postUsersToCreate.add(postUserStaging);
            }
        });
        removeAll(postUsersToRemove);
        postUsers.removeAll(postUsersToRemove);

        postUsers.addAll(createInBatch(postUsersToCreate));
        return postUsers;
    }
}
