package run.halo.app.model.vo.cern.news;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.dto.BaseMetaDTO;
import run.halo.app.model.dto.CategoryDTO;
import run.halo.app.model.dto.TagDTO;
import run.halo.app.model.dto.cern.news.NewsDetailDTO;

import java.util.List;
import java.util.Set;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NewsDetailVO extends NewsDetailDTO {
    private Set<Integer> tagIds;
    private List<TagDTO> tags;
    private Set<Integer> categoryIds;
    private List<CategoryDTO> categories;
    private Set<Long> metaIds;
    private List<BaseMetaDTO> metas;
}
