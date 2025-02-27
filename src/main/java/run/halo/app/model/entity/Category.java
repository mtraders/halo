package run.halo.app.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import run.halo.app.model.enums.cern.PostType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Category entity.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-15
 */
@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "categories", indexes = {
    @Index(name = "categories_name", columnList = "name"),
    @Index(name = "categories_parent_id", columnList = "parent_id")})
@ToString
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id",
        strategy = "run.halo.app.model.entity.support.CustomIdGenerator")
    private Integer id;

    /**
     * Category name.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Category slug name.
     */
    @Deprecated
    @Column(name = "slug_name")
    private String slugName;

    /**
     * Category slug.
     */
    @Column(name = "slug", unique = true)
    private String slug;

    /**
     * Description,can be display on category page.
     */
    @Column(name = "description", length = 100)
    private String description;

    /**
     * Cover thumbnail of the category.
     */
    @Column(name = "thumbnail", length = 1023)
    private String thumbnail;

    /**
     * Parent category.
     */
    @Column(name = "parent_id")
    @ColumnDefault("0")
    private Integer parentId;

    /**
     * Priority category.
     */
    @Column(name = "priority")
    @ColumnDefault("0")
    private Integer priority;

    /**
     * Category password.
     */
    @Column(name = "password")
    private String password;

    /**
     * Post type.
     */
    @Column(name = "post_type")
    @ColumnDefault("0")
    private PostType postType;

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

        if (null == postType) {
            postType = PostType.BASE;
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
        Category category = (Category) o;
        return id != null && Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
