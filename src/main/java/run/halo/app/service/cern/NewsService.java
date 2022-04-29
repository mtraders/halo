package run.halo.app.service.cern;

import org.springframework.lang.NonNull;
import run.halo.app.model.entity.PostMeta;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.vo.cern.news.NewsDetailVO;
import run.halo.app.model.vo.cern.news.NewsListVO;
import run.halo.app.service.base.BasePostService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface NewsService extends BasePostService<News> {
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
