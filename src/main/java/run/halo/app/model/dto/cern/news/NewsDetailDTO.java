package run.halo.app.model.dto.cern.news;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.dto.cern.CernPostDetailDTO;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.enums.cern.PostType;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NewsDetailDTO extends CernPostDetailDTO<News> {
    @Override
    public PostType getPostType() {
        return PostType.NEWS;
    }
}
