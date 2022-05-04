package run.halo.app.model.dto.cern.news;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.dto.cern.CernPostListDTO;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.enums.cern.PostType;

/**
 * News list dto.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class NewsListDTO extends CernPostListDTO<News> {
    // news special fields
    private String source;
    private String sourceLink;

    @Override
    public PostType getPostType() {
        return PostType.NEWS;
    }
}
