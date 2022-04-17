package run.halo.app.model.dto.cern;

import lombok.Data;
import lombok.ToString;
import run.halo.app.model.dto.base.OutputConverter;
import run.halo.app.model.entity.cern.InstitutionCategory;

@Data
@ToString(callSuper = true)
public class InstitutionCategoryDTO implements OutputConverter<InstitutionCategoryDTO, InstitutionCategory> {

}

