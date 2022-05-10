package run.halo.app.service.cern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.model.params.cern.paper.PaperQuery;
import run.halo.app.service.base.BasePostService;

/**
 * paper service.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
public interface PaperService extends BasePostService<Paper> {

    /**
     * Page paper.
     *
     * @param paperQuery paper query.
     * @param pageable page info.
     * @return paper page.
     */
    @NonNull
    Page<Paper> pageBy(@NonNull PaperQuery paperQuery, @NonNull Pageable pageable);
}
