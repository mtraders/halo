package run.halo.app.model.dto.cern.news;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.dto.base.OutputConverter;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.enums.PostEditorType;
import run.halo.app.model.enums.PostStatus;

import java.util.Date;

/**
 * News minimal dto.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@ToString
@EqualsAndHashCode
public class NewsListDTO implements OutputConverter<NewsListDTO, News> {
    // post minimal
    private Integer id;
    private String title;
    private PostStatus status;
    private String slug;
    private PostEditorType editorType;
    private Date updateTime;
    private Date createTime;
    private Date editTime;
    private String metaKeywords;
    private String metaDescription;
    private String fullPath;
    // post simple
    private String summary;
    private String thumbnail;
    private Long visits;
    private Boolean disallowComment;
    private String password;
    private String template;
    private Integer topPriority;
    private Long likes;
    private Long wordCount;
    private Boolean inProgress;
    // news special field
    private String source;
    private String sourceLink;

    public boolean isTopped() {
        return this.topPriority != null && this.topPriority > 0;
    }
}
