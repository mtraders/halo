package run.halo.app.service.cern.impl;

import org.springframework.stereotype.Service;
import run.halo.app.model.entity.cern.PostUser;
import run.halo.app.repository.cern.PostUserRepository;
import run.halo.app.service.base.AbstractCrudService;
import run.halo.app.service.cern.PostUserService;

@Service
public class PostUserServiceImpl extends AbstractCrudService<PostUser, Integer> implements PostUserService {
    private final PostUserRepository postUserRepository;

    public PostUserServiceImpl(PostUserRepository postUserRepository) {
        super(postUserRepository);
        this.postUserRepository = postUserRepository;
    }
}
