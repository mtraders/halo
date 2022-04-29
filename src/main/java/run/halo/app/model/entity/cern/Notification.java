package run.halo.app.model.entity.cern;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import run.halo.app.model.entity.BasePost;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Objects;

import static run.halo.app.model.support.CernConst.NOTIFICATION_POST_TYPE;

/**
 * cern notification entity.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity(name = "Notification")
@DiscriminatorValue(NOTIFICATION_POST_TYPE)
public class Notification extends BasePost {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Notification that = (Notification) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
