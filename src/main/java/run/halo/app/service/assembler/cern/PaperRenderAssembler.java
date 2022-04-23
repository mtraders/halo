package run.halo.app.service.assembler.cern;

import org.springframework.stereotype.Component;
import run.halo.app.service.ContentService;

/**
 * paper render assembler.
 *
 * @author lizc(lizc @ fits.cn)
 */
@Component
public class PaperRenderAssembler extends PaperAssembler {
    public PaperRenderAssembler(ContentService contentService) {
        super(contentService);
    }
}
