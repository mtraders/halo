package run.halo.app.model.entity.cern;

import lombok.Data;
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
import javax.persistence.Table;
import java.util.Objects;

/**
 * personnel entity.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "personnel")
public class Personnel extends BaseEntity {

    /**
     * id field.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "run.halo.app.model.entity.support.CustomIdGenerator")
    private Integer id;

    /**
     * personnel name.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * english name.
     */
    @Column(name = "english_name", nullable = false)
    private String englishName;

    /**
     * slug.
     */
    @Column(name = "slug", unique = true)
    private String slug;

    /**
     * email.
     */
    @Column(name = "email", length = 127)
    private String email;

    /**
     * Cover thumbnail of the personnel.
     */
    @Column(name = "thumbnail", length = 1023)
    private String thumbnail;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Personnel personnel = (Personnel) o;
        return id != null && Objects.equals(id, personnel.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
