package run.halo.app.model.params.cern.project;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.entity.cern.Project;
import run.halo.app.model.params.cern.CernPostQuery;

import java.util.Set;

/**
 * project query.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class ProjectQuery extends CernPostQuery<Project> {
    private Set<Integer> managerIds;
}
