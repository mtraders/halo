package run.halo.app.service.cern.impl;

import org.springframework.stereotype.Service;
import run.halo.app.model.entity.cern.News;
import run.halo.app.repository.cern.NewsRepository;
import run.halo.app.service.ContentPatchLogService;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.cern.NewsService;
import run.halo.app.service.impl.BasePostServiceImpl;

/**
 * News service impl.
 *
 * @author <a href="lizc@fists.cn">lizc</a>
 */
@Service
public class NewsServiceImpl extends BasePostServiceImpl<News> implements NewsService {

    private final NewsRepository newsRepository;
    private final ContentService contentService;
    private final ContentPatchLogService contentPatchLogService;
    private final OptionService optionService;

    /**
     * constructor of news service impl.
     *
     * @param newsRepository news repository.
     * @param optionService option service.
     * @param contentService content service.
     * @param contentPatchLogService content patch log service.
     */
    public NewsServiceImpl(NewsRepository newsRepository, OptionService optionService, ContentService contentService,
                           ContentPatchLogService contentPatchLogService) {
        super(newsRepository, optionService, contentService, contentPatchLogService);
        this.newsRepository = newsRepository;
        this.contentService = contentService;
        this.contentPatchLogService = contentPatchLogService;
        this.optionService = optionService;
    }


    /**
     * Get post with the latest content by id.
     * content from patch log.
     *
     * @param postId post id.
     * @return post with the latest content.
     */
    @Override
    public News getWithLatestContentById(Integer postId) {
        return null;
    }
}
