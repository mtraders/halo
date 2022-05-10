package run.halo.app.model.vo.cern.paper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.dto.CategoryDTO;
import run.halo.app.model.dto.TagDTO;
import run.halo.app.model.dto.cern.paper.PaperListDTO;
import run.halo.app.model.dto.cern.personnel.PersonnelDTO;

import java.util.List;

/**
 * paper list view.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class PaperListVO extends PaperListDTO {
    private List<TagDTO> tags;
    private List<CategoryDTO> categories;
    private List<PersonnelDTO> authors;
}
