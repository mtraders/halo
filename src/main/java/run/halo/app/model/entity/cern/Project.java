package run.halo.app.model.entity.cern;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.entity.BasePost;

/**
 * project entity.
 *
 * @author lizc(lizc@fists.cn)
 */

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity(name = "Project")
@DiscriminatorValue("2")
public class Project extends BasePost {
    private String projectSource;
}
