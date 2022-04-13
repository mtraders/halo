package run.halo.app.model.entity.cern;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.entity.BasePost;

/**
 * News entity.
 *
 * @author lizc(lizc@fists.cn)
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity(name = "News")
@DiscriminatorValue("2")
public class News extends BasePost {

    /**
     * source of the news.
     */
    @Column(name = "source")
    private String source;

    /**
     * link of the source.
     */
    @Column(name = "link")
    private String link;

    @Override
    public void prePersist() {
        super.prePersist();
        if (source == null) {
            source = StringUtils.EMPTY;
        }
        if (link == null) {
            link = StringUtils.EMPTY;
        }
    }
}
