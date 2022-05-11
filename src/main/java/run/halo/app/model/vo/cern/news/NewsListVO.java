package run.halo.app.model.vo.cern.news;

import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.model.dto.CategoryDTO;
import run.halo.app.model.dto.TagDTO;
import run.halo.app.model.dto.cern.news.NewsListDTO;

import java.util.List;
import java.util.Map;

/**
 * News list vo.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NewsListVO extends NewsListDTO {
    private List<TagDTO> tags;
    private List<CategoryDTO> categories;
    private Map<String, Object> metas;
}
