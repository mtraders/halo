package run.halo.app.model.params.cern.news;

import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.params.cern.CernPostQuery;

/**
 * News query.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NewsQuery extends CernPostQuery<News> {
    private String source;
}
