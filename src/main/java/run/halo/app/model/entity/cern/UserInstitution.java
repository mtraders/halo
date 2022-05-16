package run.halo.app.model.entity.cern;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import run.halo.app.model.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Institution user.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Entity
@Table(name = "user_institutions", indexes = {@Index(name = "user_institutions_institution_id", columnList = "institution_id"),
    @Index(name = "user_institutions_user_id", columnList = "user_id")})
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class UserInstitution extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "run.halo.app.model.entity.support.CustomIdGenerator")
    private Integer id;

    /**
     * Institution id.
     */
    @Column(name = "institution_id")
    private Integer institutionId;

    /**
     * User id.
     */
    @Column(name = "user_id")
    private Integer userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        UserInstitution that = (UserInstitution) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(institutionId, userId);
    }
}
