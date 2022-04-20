package run.halo.app.model.dto.cern.paper;

import lombok.Data;
import lombok.ToString;
import run.halo.app.model.dto.base.OutputConverter;
import run.halo.app.model.entity.cern.Paper;

@Data
@ToString
public class PaperListDTO implements OutputConverter<PaperListDTO, Paper> {
    private Integer id;
    private String title;
    private String publisher;
}
