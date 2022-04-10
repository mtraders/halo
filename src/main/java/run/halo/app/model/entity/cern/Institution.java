package run.halo.app.model.entity.cern;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import run.halo.app.model.entity.BaseEntity;
import run.halo.app.model.enums.cern.InstitutionType;

/**
 * Institutions entity.
 *
 * @author lizc(lizc@fists.cn)
 */
public class Institution extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "run.halo.app.model.entity.support"
            + ".CustomIdGenerator")
    private Integer id;

    /**
     * Institution name.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * institution slug.
     */
    @Column(name = "slug", unique = true)
    private String slug;

    /**
     * Priority institution.
     */
    @Column(name = "priority")
    @ColumnDefault("0")
    private Integer priority;

    /**
     * Description,can be display on institution page.
     */
    @Column(name = "description", length = 100)
    private String description;

    /**
     * Post editor type.
     */
    @Column(name = "institution_type")
    @ColumnDefault("1")
    private InstitutionType institutionType;

    @Override
    public void prePersist() {
        super.prePersist();
        if (priority == null) {
            this.priority = 0;
        }
        if (description == null) {
            this.description = StringUtils.EMPTY;
        }
        if (institutionType == null) {
            this.institutionType = InstitutionType.STATION;
        }
    }

}
