package run.halo.app.model.projection.cern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

/**
 * personnel post count projection.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@AllArgsConstructor
public class PersonnelPostCountProjection {
    @Column(name = "personnel_id")
    private int personnelId;
    @Column(name = "post_count")
    private long postCount;
    @Column(name = "post_type")
    private int postType;

    public PersonnelPostCountProjection() {
        this.postCount = 0L;
    }
}
