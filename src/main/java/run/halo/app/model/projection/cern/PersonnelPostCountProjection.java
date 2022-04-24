package run.halo.app.model.projection.cern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * personnel post count projection.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonnelPostCountProjection {
    private Integer personnelId;
    private Long postCount;
    private Integer postType;
}
