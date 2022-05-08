package run.halo.app.model.params.cern;

import lombok.Data;
import run.halo.app.model.entity.BasePost;
import run.halo.app.model.enums.PostStatus;

import java.util.Set;

/**
 * Cern post query.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
public class CernPostQuery<T extends BasePost> {
    protected String keyword;
    protected Set<PostStatus> statuses;
    protected Integer categoryId;
}
