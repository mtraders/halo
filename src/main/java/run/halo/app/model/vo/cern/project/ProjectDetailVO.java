package run.halo.app.model.vo.cern.project;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.dto.CategoryDTO;
import run.halo.app.model.dto.TagDTO;
import run.halo.app.model.dto.cern.personnel.PersonnelDTO;
import run.halo.app.model.dto.cern.project.ProjectDetailDTO;

import java.util.List;

/**
 * project detail vo.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class ProjectDetailVO extends ProjectDetailDTO {
    private List<TagDTO> tags;
    private List<CategoryDTO> categories;
    private List<PersonnelDTO> managers;
}
