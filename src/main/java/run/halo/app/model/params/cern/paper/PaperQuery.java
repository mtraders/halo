package run.halo.app.model.params.cern.paper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.model.params.cern.CernPostQuery;

import java.util.Set;

/**
 * paper query.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaperQuery extends CernPostQuery<Paper> {
    private Set<Integer> authorIds;
}
