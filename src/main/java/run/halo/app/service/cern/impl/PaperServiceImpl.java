package run.halo.app.service.cern.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import run.halo.app.model.entity.Content;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.model.params.cern.paper.PaperQuery;
import run.halo.app.repository.cern.PaperRepository;
import run.halo.app.service.ContentPatchLogService;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.assembler.cern.PaperAssembler;
import run.halo.app.service.cern.PaperService;
import run.halo.app.service.impl.BasePostServiceImpl;


/**
 * paper service impl.
 *
 * @author lizc
 */
@Service
public class PaperServiceImpl extends BasePostServiceImpl<Paper> implements PaperService {

    private final PaperRepository paperRepository;
    private final ContentPatchLogService contentPatchLogService;
    private final PaperAssembler paperAssembler;

    /**
     * base post repository constructor.
     *
     * @param paperRepository paper repository.
     * @param optionService option service.
     * @param contentService content service.
     * @param contentPatchLogService content path log service.
     * @param paperAssembler paper assembler.
     */
    public PaperServiceImpl(PaperRepository paperRepository, OptionService optionService, ContentService contentService,
                            ContentPatchLogService contentPatchLogService, PaperAssembler paperAssembler) {
        super(paperRepository, optionService, contentService, contentPatchLogService);
        this.paperRepository = paperRepository;
        this.contentPatchLogService = contentPatchLogService;
        this.paperAssembler = paperAssembler;
    }

    /**
     * Get post with the latest content by id.
     * content from patch log.
     *
     * @param postId post id.
     * @return post with the latest content.
     */
    @Override
    public Paper getWithLatestContentById(Integer postId) {
        Paper paper = getById(postId);
        Content content = getContentById(postId);
        Content.PatchedContent patchedContent = contentPatchLogService.getPatchedContentById(content.getHeadPatchLogId());
        paper.setContent(patchedContent);
        return paper;
    }

    /**
     * Page paper.
     *
     * @param paperQuery paper query.
     * @param pageable page info.
     * @return paper page.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @NonNull
    public Page<Paper> pageBy(@NonNull PaperQuery paperQuery, @NonNull Pageable pageable) {
        Assert.notNull(paperQuery, "Paper query must not be null");
        Assert.notNull(pageable, "Paper page info must not be null");
        Specification<Paper> paperSpecification = paperAssembler.buildSpecByQuery(paperQuery);
        return paperRepository.findAll(paperSpecification, pageable);
    }
}
