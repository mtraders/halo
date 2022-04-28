package run.halo.app.model.params.cern;

import com.google.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import run.halo.app.model.dto.base.InputConverter;
import run.halo.app.model.entity.PostMeta;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.enums.PostEditorType;
import run.halo.app.model.params.BasePostParam;
import run.halo.app.model.params.PostMetaParam;
import run.halo.app.utils.SlugUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class NewsParam extends BasePostParam implements InputConverter<News> {
    // news special fields
    @Size(max = 255, message = "新闻来源长度不能超过 {max}")
    private String source;
    @Size(max = 255, message = "新闻链接长度不能超过 {max}")
    private String link;
    // related fields.
    private Set<Integer> tagIds;
    private Set<Integer> categoryIds;
    private Set<PostMetaParam> metas;

    @Override
    @Size(max = 255, message = "新闻别名的字符长度不能超过 {max}")
    public String getSlug() {
        return super.getSlug();
    }

    @Override
    @NotBlank(message = "新闻标题不能为空")
    @Size(max = 100, message = "新闻标题的字符长度不能超过 {max}")
    public String getTitle() {
        return super.getTitle();
    }

    /**
     * get post metas.
     *
     * @return PostMeta Set of the news.
     */
    public Set<PostMeta> getPostMetas() {
        Set<PostMeta> postMetaSet = Sets.newHashSet();
        if (CollectionUtils.isEmpty(metas)) {
            return postMetaSet;
        }
        for (PostMetaParam postMetaParam : metas) {
            PostMeta postMeta = postMetaParam.convertTo();
            postMetaSet.add(postMeta);
        }
        return postMetaSet;
    }

    @Override
    public News convertTo() {
        slug = StringUtils.isBlank(slug) ? SlugUtils.slug(title) : SlugUtils.slug(slug);

        if (null == thumbnail) {
            thumbnail = "";
        }

        if (null == editorType) {
            editorType = PostEditorType.MARKDOWN;
        }
        News news = InputConverter.super.convertTo();
        populateContent(news);
        return news;
    }

    @Override
    public void update(News news) {
        slug = StringUtils.isBlank(slug) ? SlugUtils.slug(title) : SlugUtils.slug(slug);

        if (null == thumbnail) {
            thumbnail = "";
        }

        if (null == editorType) {
            editorType = PostEditorType.MARKDOWN;
        }
        populateContent(news);
        InputConverter.super.update(news);
    }

}
