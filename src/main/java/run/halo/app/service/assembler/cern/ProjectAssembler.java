package run.halo.app.service.assembler.cern;

import org.springframework.stereotype.Component;
import run.halo.app.model.entity.cern.Project;
import run.halo.app.service.CategoryService;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;

/**
 * project assembler.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Component
public class ProjectAssembler extends CernPostAssembler<Project> {
    /**
     * constructor of cern query service.
     *
     * @param categoryService category service.
     * @param contentService content service.
     * @param optionService option service.
     */
    public ProjectAssembler(CategoryService categoryService, ContentService contentService,
                            OptionService optionService) {
        super(categoryService, contentService, optionService);
    }
}
