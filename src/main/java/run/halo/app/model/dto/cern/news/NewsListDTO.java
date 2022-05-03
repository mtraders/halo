package run.halo.app.model.dto.cern.news;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.dto.cern.CernPostListDTO;
import run.halo.app.model.entity.cern.News;

/**
 * News list dto.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class NewsListDTO extends CernPostListDTO<News> {
    // news special field
    private String source;
    private String sourceLink;

    public boolean isTopped() {
        return this.getTopPriority() != null && this.getTopPriority() > 0;
    }
}
