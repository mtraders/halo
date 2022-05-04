package run.halo.app.model.params.cern;

import lombok.Data;
import run.halo.app.model.enums.PostStatus;

import java.util.Set;

/**
 * News query.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
public class NewsQuery {
    private String keyword;
    private Set<PostStatus> status;
    private Integer categoryId;
    private String source;
}
