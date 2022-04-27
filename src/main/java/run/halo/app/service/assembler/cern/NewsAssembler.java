package run.halo.app.service.assembler.cern;

import org.springframework.stereotype.Component;
import run.halo.app.model.entity.cern.News;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.assembler.BasePostAssembler;

@Component
public class NewsAssembler extends BasePostAssembler<News> {
    private final ContentService contentService;

    public NewsAssembler(ContentService contentService, OptionService optionService) {
        super(contentService, optionService);
        this.contentService = contentService;
    }
}
