package run.halo.app.model.entity.cern;

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
    private String name;

    /**
     * english name.
     */
    private String englishName;

    /**
     * slug.
     */
    private String slug;

    /**
     * email.
     */
    private String email;

    /**
     * Cover thumbnail of the personnel.
     */
    private String thumbnail;
}
