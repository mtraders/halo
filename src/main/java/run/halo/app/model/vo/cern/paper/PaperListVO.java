package run.halo.app.model.vo.cern.paper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.dto.cern.personnel.PersonnelDTO;
import run.halo.app.model.vo.PostListVO;

import java.util.List;

/**
 * paper list view.
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class PaperListVO extends PostListVO {
    private List<PersonnelDTO> authors;
}
