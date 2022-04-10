package run.halo.app.model.entity.cern;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.entity.BaseEntity;

/**
 * Institution Category entity.
 *
 * @author lizc(lizc@fists.cn)
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class InstitutionCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "run.halo.app.model.entity.support"
            + ".CustomIdGenerator")
    private Integer id;

    /**
     * catetory name.
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
}
