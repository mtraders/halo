package run.halo.app.model.dto.cern.news;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.dto.post.BasePostDetailDTO;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NewsDTO extends BasePostDetailDTO {
    private String source;
    private String link;
}
