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
 * Post personnel.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Entity
@Table(name = "post_personnel", indexes = {@Index(name = "post_personnel_post_id", columnList = "post_id"),
    @Index(name = "post_personnel_personnel_id", columnList = "personnel_id")})
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class PostPersonnel extends BaseEntity {
    /**
     * id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "run.halo.app.model.entity.support.CustomIdGenerator")
    private Integer id;
    /**
     * Post id.
     */
    @Column(name = "post_id", nullable = false)
    private Integer postId;

    /**
     * Personnel id.
     */
    @Column(name = "personnel_id", nullable = false)
    private Integer personnelId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        PostPersonnel that = (PostPersonnel) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, personnelId);
    }
}
