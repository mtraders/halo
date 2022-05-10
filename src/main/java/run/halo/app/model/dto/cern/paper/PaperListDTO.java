package run.halo.app.model.dto.cern.paper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.dto.cern.CernPostListDTO;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.model.enums.cern.PostType;

import java.util.Date;

/**
 * Paper list dto.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class PaperListDTO extends CernPostListDTO<Paper> {
    private String publisher;
    private Date publishDate;

    @Override
    public PostType getPostType() {
        return PostType.PAPER;
    }
}
