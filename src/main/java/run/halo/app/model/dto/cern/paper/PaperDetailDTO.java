package run.halo.app.model.dto.cern.paper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.dto.base.OutputConverter;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.model.enums.PostEditorType;
import run.halo.app.model.enums.PostStatus;

import java.util.Date;

/**
 * paper detail dto.
 */
@Data
@ToString
@EqualsAndHashCode
public class PaperDetailDTO implements OutputConverter<PaperDetailDTO, Paper> {
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
    private String summary;
    private String thumbnail;
    private Long visits;
    private String template;
    private Integer topPriority;
    private Long likes;
    private Long wordCount;
    private Boolean inProgress;
    private String originalContent;
    private String content;
    private Long commentCount;
    private Date publishDate;
    private String press;

    public boolean isTopped() {
        return this.topPriority != null && this.topPriority > 0;
    }
}
