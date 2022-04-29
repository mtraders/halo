package run.halo.app.service.cern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import run.halo.app.model.entity.PostMeta;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.params.PostQuery;
import run.halo.app.model.vo.cern.news.NewsDetailVO;
import run.halo.app.service.base.BasePostService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface NewsService extends BasePostService<News> {

    /**
     * pages news.
     *
     * @param postQuery post query.
     * @param pageable pageable.
     * @return news list vo.
     */
    @NonNull
    Page<News> pageBy(@NonNull PostQuery postQuery, @NonNull Pageable pageable);

    /**
     * Pages news by keyword.
     *
     * @param keyword keyword
     * @param pageable pageable
     * @return a page of news
     */
    @NonNull
    Page<News> pageBy(@NonNull String keyword, @NonNull Pageable pageable);

    /**
     * Create news by news param.
     *
     * @param news news must not be null
     * @param tagIds tag id set
     * @param categoryIds category id set
     * @param metas metas
     * @param autoSave auto save
     * @return news created detail.
     */
    @NonNull
    NewsDetailVO createBy(@NonNull News news, Set<Integer> tagIds, Set<Integer> categoryIds, Set<PostMeta> metas, boolean autoSave);

    /**
     * Updates news by news, tag id set and category id set.
     *
     * @param newsToUpdate news to update
     * @param tagIds tag id set
     * @param categoryIds category id set
     * @param metas metas
     * @param autoSave auto save
     * @return updated news
     */
    @NonNull
    NewsDetailVO updateBy(@NonNull News newsToUpdate, Set<Integer> tagIds, Set<Integer> categoryIds, Set<PostMeta> metas, boolean autoSave);

    /**
     * Remove news in batch.
     *
     * @param ids ids must not be null.
     * @return a list of deleted news.
     */
    @NonNull
    List<News> removeByIds(@NonNull Collection<Integer> ids);

}
