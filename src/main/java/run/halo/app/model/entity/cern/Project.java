package run.halo.app.model.entity.cern;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;

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
@DiscriminatorValue("4")
public class Project extends BasePost {

    /**
     * 实施时间.
     */
    @Column(name = "peroid")
    private String peroid;

    /**
     * 项目来源.
     */
    @Column(name = "source")
    private String source;

    @Override
    public void prePersist() {
        super.prePersist();

        if (peroid == null) {
            peroid = StringUtils.EMPTY;
        }

        if (source == null) {
            source = StringUtils.EMPTY;
        }
    }
}
