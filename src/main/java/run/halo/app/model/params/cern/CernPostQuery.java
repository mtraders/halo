package run.halo.app.model.params.cern;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.enums.cern.PostType;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * Cern post query.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
public class CernPostQuery {

    private String keyword;

    private Set<PostStatus> status;

    private Integer categoryId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    private PostType type;

    /**
     * get post type.
     *
     * @return post type.
     */
    public PostType getType() {
        if (Objects.isNull(type)) {
            return PostType.BASE;
        }
        return type;
    }

    /**
     * set post type.
     *
     * @param type post type.
     */
    public void setType(PostType type) {
        this.type = Objects.requireNonNullElse(type, PostType.BASE);
    }

}
