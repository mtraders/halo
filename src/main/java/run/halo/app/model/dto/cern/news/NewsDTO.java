package run.halo.app.model.dto.cern.news;

import lombok.Data;
import lombok.ToString;
import run.halo.app.model.dto.base.OutputConverter;
import run.halo.app.model.entity.cern.News;
import run.halo.app.model.enums.PostEditorType;
import run.halo.app.model.enums.PostStatus;

import java.util.Date;

@Data
@ToString(callSuper = true)
public class NewsDTO implements OutputConverter<NewsDTO, News> {
    // base post fields
    private Integer id;
    private String title;
    private PostStatus status;
    private String slug;
    private PostEditorType editorType;
    private String originalContent;
    private String formattedContent;
    private String summary;
    private String thumbnail;
    private Long visits;
    private Integer topPriority;
    private Long likes;
    private Date editTime;
    private String metaKeywords;
    private String metaDescription;
    private Long wordCount;
    // news special fields
    private String source;
    private String link;
    // comment date fields
    private Date createTime;
    private Date updateTime;
}
