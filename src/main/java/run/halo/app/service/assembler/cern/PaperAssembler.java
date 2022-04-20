package run.halo.app.service.assembler.cern;


import org.springframework.stereotype.Component;
import run.halo.app.service.ContentService;

@Component
public class PaperAssembler {
    private final ContentService contentService;

    public PaperAssembler(ContentService contentService) {
        this.contentService = contentService;
    }
}
