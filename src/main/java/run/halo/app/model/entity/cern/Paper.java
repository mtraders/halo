package run.halo.app.model.entity.cern;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import run.halo.app.model.entity.BasePost;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Date;
import java.util.Objects;

import static run.halo.app.model.support.CernConst.PAPER_POST_TYPE;

/**
 * paper entity.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity(name = "Paper")
@DiscriminatorValue(PAPER_POST_TYPE)
public class Paper extends BasePost {

    /**
     * publish date.
     */
    @Column(name = "publish_date")
    private Date publishDate;

    /**
     * publisher.
     */
    @Column(name = "publisher")
    private String publisher;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Paper paper = (Paper) o;
        return getId() != null && Objects.equals(getId(), paper.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
