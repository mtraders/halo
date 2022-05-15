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
 * Post user.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Entity
@Table(name = "post_users", indexes = {@Index(name = "post_users_post_id", columnList = "post_id"),
    @Index(name = "post_users_user_id", columnList = "user_id")})
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PostUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "run.halo.app.model.entity.support.CustomIdGenerator")
    private Integer id;

    /**
     * User id.
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * Post id.
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
        PostUser postUser = (PostUser) o;
        return id != null && Objects.equals(id, postUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, postId);
    }
}
