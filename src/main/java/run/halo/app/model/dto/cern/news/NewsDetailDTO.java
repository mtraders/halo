package run.halo.app.model.dto.cern.news;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.dto.cern.CernPostDetailDTO;
import run.halo.app.model.entity.cern.News;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NewsDetailDTO extends CernPostDetailDTO<News>  {
}
