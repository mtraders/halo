package run.halo.app.model.dto.cern.project;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.dto.cern.CernPostListDTO;
import run.halo.app.model.entity.cern.Project;
import run.halo.app.model.enums.cern.PostType;

/**
 * project list dto.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class ProjectListDTO extends CernPostListDTO<Project> {
    private String source;
    private String period;

    @Override
    public PostType getPostType() {
        return PostType.PROJECT;
    }
}
