package run.halo.app.service.cern.impl;

import org.springframework.stereotype.Service;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.repository.base.BasePostRepository;
import run.halo.app.service.ContentPatchLogService;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.cern.PaperService;
import run.halo.app.service.impl.BasePostServiceImpl;


@Service
public class PaperServiceImpl extends BasePostServiceImpl<Paper> implements PaperService {

    /**
     * base post repository constructor.
     *
     * @param basePostRepository base post repository.
     * @param optionService option service.
     * @param contentService content service.
     * @param contentPatchLogService content path log service.
     */
    public PaperServiceImpl(BasePostRepository<Paper> basePostRepository,
                            OptionService optionService, ContentService contentService,
                            ContentPatchLogService contentPatchLogService) {
        super(basePostRepository, optionService, contentService, contentPatchLogService);
    }

    @Override
    public Paper getWithLatestContentById(Integer postId) {
        return null;
    }
}
