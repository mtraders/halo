package run.halo.app.service.cern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.model.params.cern.paper.PaperQuery;
import run.halo.app.model.vo.cern.paper.PaperDetailVO;
import run.halo.app.service.base.BasePostService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    /**
     * create paper by paper param.
     *
     * @param paper paper entity.
     * @param tagIds tag id list.
     * @param categoryIds category id list.
     * @param authorIds author id list.
     * @param autoSave auto-save or not
     * @return paper detail vo.
     */
    @NonNull
    PaperDetailVO createBy(@NonNull Paper paper, Set<Integer> tagIds, Set<Integer> categoryIds, Set<Integer> authorIds, boolean autoSave);

    /**
     * update paper by paper param.
     *
     * @param paper paper entity.
     * @param tagIds tag id list.
     * @param categoryIds category id list.
     * @param authorIds author id list.
     * @param autoSave auto-save or not
     * @return paper detail vo.
     */
    @NonNull
    PaperDetailVO updateBy(@NonNull Paper paper, Set<Integer> tagIds, Set<Integer> categoryIds, Set<Integer> authorIds, boolean autoSave);

    /**
     * remove paper by ids.
     *
     * @param ids paper ids.
     * @return delete papers.
     */
    @NonNull
    List<Paper> removeByIds(@Nullable Collection<Integer> ids);

}
