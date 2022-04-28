package run.halo.app.service.assembler.cern;

import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.vo.cern.news.NewsListVO;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.assembler.BasePostAssembler;

import java.util.List;

@Component
public class NewsAssembler extends BasePostAssembler<News> {
    private final ContentService contentService;

    public NewsAssembler(ContentService contentService, OptionService optionService) {
        super(contentService, optionService);
        this.contentService = contentService;
    }

    @NonNull
    public Page<NewsListVO> convertToListVo(Page<News> newsPage) {
        Assert.notNull(newsPage, "News page must not be null");
        List<News> newsList = newsPage.getContent();
        return null;
    }


}
