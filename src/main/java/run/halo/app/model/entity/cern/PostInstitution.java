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
 * Institution posts.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Entity
@Table(name = "post_institutions", indexes = {@Index(name = "post_institutions_institution_id", columnList = "institution_id"),
    @Index(name = "post_institutions_post_id", columnList = "post_id")})
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PostInstitution extends BaseEntity {
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
    @Column(name = "post_id")
    private Integer postId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        PostInstitution that = (PostInstitution) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(institutionId, postId);
    }
}
