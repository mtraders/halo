package run.halo.app.model.params;

import lombok.Data;
import org.springframework.util.CollectionUtils;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.enums.cern.PostType;

import java.util.HashSet;
import java.util.Set;

/**
 * Post query.
 *
 * @author johnniang
 * @author guqing
 * @date 4/10/19
 */
@Data
public class PostQuery {

    /**
     * Keyword.
     */
    private String keyword;

    /**
     * Post status.
     */
    @Deprecated(forRemoval = true, since = "1.5.0")
    private PostStatus status;

    /**
     * Post statuses.
     */
    private Set<PostStatus> statuses;

    /**
     * Category id.
     */
    private Integer categoryId;

    /**
     * User id.
     */
    private Integer userId;
    /**
     * cern post type.
     */
    private Set<PostType> postTypes;

    /**
     * This method is deprecated in version 1.5.0, and it is recommended to use
     * <code>getStatuses()</code> method.
     *
     * @return post status.
     * @see #getStatuses()
     */
    @Deprecated
    public PostStatus getStatus() {
        return status;
    }

    /**
     * In order to be compatible with status, this method will return the combined results
     * of status and statuses before status is removed.
     *
     * @return a combined status set of status and statues
     */
    public Set<PostStatus> getStatuses() {
        Set<PostStatus> statuses = new HashSet<>();
        // Need to be compatible with status parameter values due to historical reasons.
        if (this.status != null) {
            statuses.add(this.status);
        }
        if (!CollectionUtils.isEmpty(this.statuses)) {
            statuses.addAll(this.statuses);
        }
        return statuses;
    }
}
