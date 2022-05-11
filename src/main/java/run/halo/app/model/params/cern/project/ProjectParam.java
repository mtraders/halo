package run.halo.app.model.params.cern.project;

import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.model.dto.base.InputConverter;
import run.halo.app.model.entity.cern.Project;
import run.halo.app.model.params.cern.CernPostParam;

/**
 * project param.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectParam extends CernPostParam implements InputConverter<Project> {
    /**
     * Convert to domain.(shallow)
     *
     * @return new domain with same value(not null)
     */
    @Override
    public Project convertTo() {
        checkFormat();
        Project project = InputConverter.super.convertTo();
        populateContent(project);
        return project;
    }

    /**
     * Update a domain by dto.(shallow)
     *
     * @param domain updated domain
     */
    @Override
    public void update(Project domain) {
        checkFormat();
        populateContent(domain);
        InputConverter.super.update(domain);
    }
}
