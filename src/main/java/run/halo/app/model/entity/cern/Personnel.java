package run.halo.app.model.entity.cern;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.entity.BaseEntity;

/**
 * personnel entity.
 *
 * @author lizc(lizc@fists.cn)
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@Table(name = "personnel")
public class Personnel extends BaseEntity {

    /**
     * id field.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "run.halo.app.model.entity.support.CustomIdGenerator")
    private Long id;

    /**
     * personnel name.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * english name.
     */
    @Column(name = "english_name", unique = true)
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
}
