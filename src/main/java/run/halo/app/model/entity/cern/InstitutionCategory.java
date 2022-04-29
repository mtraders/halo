package run.halo.app.model.entity.cern;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;
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
 * Institution Category entity.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "institution_categories", indexes = {
    @Index(name = "categories_name", columnList = "name"),
    @Index(name = "categories_parent_id", columnList = "parent_id")})
public class InstitutionCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "run.halo.app.model.entity.support"
        + ".CustomIdGenerator")
    private Integer id;

    /**
     * category name.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Cover thumbnail of the category.
     */
    @Column(name = "thumbnail", length = 1023)
    private String thumbnail;

    /**
     * Description,can be display on category page.
     */
    @Column(name = "description", length = 100)
    private String description;

    /**
     * Priority category.
     */
    @Column(name = "priority")
    @ColumnDefault("0")
    private Integer priority;

    /**
     * Category slug.
     */
    @Column(name = "slug", unique = true)
    private String slug;

    /**
     * Parent category.
     */
    @Column(name = "parent_id")
    @ColumnDefault("0")
    private Integer parentId;

    @Override
    public void prePersist() {
        super.prePersist();

        if (description == null) {
            description = "";
        }

        if (parentId == null || parentId < 0) {
            parentId = 0;
        }

        if (priority == null) {
            priority = 0;
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
        InstitutionCategory that = (InstitutionCategory) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
