package run.halo.app.model.params.cern;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import run.halo.app.model.entity.BasePost;
import run.halo.app.model.entity.Content;
import run.halo.app.model.enums.PostEditorType;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.utils.MarkdownUtils;
import run.halo.app.utils.SlugUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * Cern post param.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
public abstract class CernPostParam {
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 100, message = "文章标题的字符长度不能超过 {max}")
    protected String title;
    @Size(max = 255, message = "文章别名的字符长度不能超过 {max}")
    protected String slug;
    protected String summary;
    @Size(max = 1023, message = "封面图链接的字符长度不能超过 {max}")
    protected String thumbnail;
    @Min(value = 0, message = "排序字段值不能小于 {value}")
    protected Integer topPriority;
    protected String metaKeywords;
    protected String metaDescription;
    protected PostEditorType editorType;
    protected String content;
    protected String originalContent;
    protected Date createTime;
    protected Date editTime;
    protected PostStatus status = PostStatus.DRAFT;
    /**
     * if {@code true}, it means is that do not let the back-end render the original content
     * because the content has been rendered, and you only need to store the original content.
     */
    protected Boolean keepRaw = true;
    protected Set<Integer> tagIds;
    protected Set<Integer> categoryIds;

    /**
     * check & format data of this entity.
     */
    protected void checkFormat() {
        slug = StringUtils.isBlank(slug) ? SlugUtils.slug(title) : SlugUtils.slug(slug);
        if (null == thumbnail) {
            thumbnail = "";
        }

        if (null == editorType) {
            editorType = PostEditorType.RICHTEXT;
        }
    }

    protected <T extends BasePost> void populateContent(T post) {
        Assert.notNull(post, "The post must not be null.");

        Content postContent = new Content();
        postContent.setOriginalContent(originalContent);

        if (Objects.equals(keepRaw, false)
            && PostEditorType.MARKDOWN.equals(editorType)) {
            postContent.setContent(MarkdownUtils.renderHtml(originalContent));
        } else if (PostEditorType.RICHTEXT.equals(editorType)) {
            postContent.setContent(originalContent);
        } else {
            postContent.setContent(content);
        }
        post.setContent(Content.PatchedContent.of(postContent));
    }

}
