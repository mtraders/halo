package run.halo.app.model.params;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.google.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import run.halo.app.model.dto.base.InputConverter;
import run.halo.app.model.entity.Post;
import run.halo.app.model.entity.PostMeta;
import run.halo.app.model.enums.PostEditorType;
import run.halo.app.model.enums.cern.PostType;
import run.halo.app.model.support.NotAllowSpaceOnly;
import run.halo.app.utils.SlugUtils;

/**
 * Post param.
 *
 * @author johnniang
 * @author ryanwang
 * @author guqing
 * @date 2019-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PostParam extends BasePostParam implements InputConverter<Post> {

    private Set<Integer> tagIds;

    private Set<Integer> categoryIds;

    private Set<PostMetaParam> metas;

    // add user ids
    private Set<Integer> userIds = Sets.newHashSet();

    // cern post fields
    private PostType postType;
    @Size(max = 1023, message = "文章来源的字符长度不能超过 {max}")
    private String postSource;
    @Size(max = 1023, message = "文章来源链接的字符长度不能超过 {max}")
    private String postSourceLink;
    @Size(max = 255, message = "项目周期长度不能超过 {max}")
    private String projectPeriod;
    @Size(max = 255, message = "项目来源长度不能超过 {max}")
    private String projectSource;
    @Size(max = 255, message = "项目经理长度不能超过 {max}")
    private String projectManager;
    private Date paperPublishDate;
    @Size(max = 255, message = "出版社长度不能超过 {max}")
    private String paperPublisher;
    @Size(max = 255, message = "作者长度不能超过 {max}")
    private String paperAuthors;

    @Override
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 100, message = "文章标题的字符长度不能超过 {max}")
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    @Size(max = 255, message = "文章别名的字符长度不能超过 {max}")
    public String getSlug() {
        return super.getSlug();
    }

    @Override
    @Size(max = 255, message = "文章密码的字符长度不能超过 {max}")
    @NotAllowSpaceOnly(message = "密码开头和结尾不能包含空字符串")
    public String getPassword() {
        return super.getPassword();
    }

    /**
     * get post metas.
     *
     * @return PostMeta Set.
     */
    public Set<PostMeta> getPostMetas() {
        Set<PostMeta> postMetaSet = new HashSet<>();
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
    public Post convertTo() {
        slug = StringUtils.isBlank(slug) ? SlugUtils.slug(title) : SlugUtils.slug(slug);

        if (null == thumbnail) {
            thumbnail = "";
        }

        if (null == editorType) {
            editorType = PostEditorType.RICHTEXT;
        }

        // cern fields fill
        if (postType != PostType.NEWS) {
            postSource = "";
            postSourceLink = "";
        }
        if (postType != PostType.PAPER) {
            paperPublisher = "";
            paperPublishDate = null;
            paperAuthors = null;
        }
        if (postType != PostType.PROJECT) {
            projectPeriod = "";
            projectSource = "";
            projectManager = "";
        }

        Post post = InputConverter.super.convertTo();
        populateContent(post);
        return post;
    }

    @Override
    public void update(Post post) {
        slug = StringUtils.isBlank(slug) ? SlugUtils.slug(title) : SlugUtils.slug(slug);

        if (null == thumbnail) {
            thumbnail = "";
        }

        if (null == editorType) {
            editorType = PostEditorType.RICHTEXT;
        }
        populateContent(post);
        InputConverter.super.update(post);
    }
}
