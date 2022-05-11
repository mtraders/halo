package run.halo.app.model.entity.cern;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import run.halo.app.model.entity.BasePost;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Objects;

import static run.halo.app.model.support.CernConst.PROJECT_POST_TYPE;

/**
 * project entity.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity(name = "Project")
@DiscriminatorValue(PROJECT_POST_TYPE)
public class Project extends BasePost {

    /**
     * 实施时间.
     */
    @Column(name = "period")
    private String period;

    /**
     * 项目来源.
     */
    @Column(name = "source")
    private String source;

    @Override
    public void prePersist() {
        super.prePersist();

        if (period == null) {
            period = StringUtils.EMPTY;
        }

        if (source == null) {
            source = StringUtils.EMPTY;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Project project = (Project) o;
        return getId() != null && Objects.equals(getId(), project.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
