package run.halo.app.model.params.cern;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.enums.cern.PostType;

import java.util.Date;
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

    public PostType getType() {
        if (type == null) {
            return PostType.BASE;
        }
        return type;
    }

    public void setType(PostType type) {
        if (type == null) {
            this.type = PostType.BASE;
        } else {
            this.type = type;
        }
    }

}
