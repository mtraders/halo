package run.halo.app.model.entity.cern;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import run.halo.app.model.entity.BasePost;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Objects;

import static run.halo.app.model.support.CernConst.NEWS_POST_TYPE;

/**
 * News entity.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity(name = "News")
@DiscriminatorValue(NEWS_POST_TYPE)
public class News extends BasePost {

    /**
     * source of the news.
     */
    @Column(name = "source")
    private String source;

    /**
     * link of the source.
     */
    @Column(name = "source_link")
    private String sourceLink;

    @Override
    public void prePersist() {
        super.prePersist();
        if (source == null) {
            source = StringUtils.EMPTY;
        }
        if (sourceLink == null) {
            sourceLink = StringUtils.EMPTY;
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
        News news = (News) o;
        return getId() != null && Objects.equals(getId(), news.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
