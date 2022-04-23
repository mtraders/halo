package run.halo.app.model.entity.cern;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.entity.BasePost;

/**
 * paper entity.
 *
 * @author lizc(lizc@fists.cn)
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity(name = "Paper")
@DiscriminatorValue("4")
public class Paper extends BasePost {

    /**
     * publish date.
     */
    @Column(name = "publish_date")
    private Date publishDate;

    /**
     * press.
     */
    @Column(name = "press")
    private String press;

}
