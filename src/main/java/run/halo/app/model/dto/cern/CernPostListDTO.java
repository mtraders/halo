package run.halo.app.model.dto.cern;

import lombok.Data;
import org.springframework.lang.NonNull;
import run.halo.app.model.dto.base.OutputConverter;
import run.halo.app.model.entity.BasePost;
import run.halo.app.model.enums.PostEditorType;
import run.halo.app.model.enums.PostStatus;

import java.util.Date;

@Data
public abstract class CernPostListDTO<POST extends BasePost> implements OutputConverter<CernPostListDTO<POST>, POST> {
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
    private Boolean disallowComment;
    private String password;
    private String template;
    private Integer topPriority;
    private Long likes;
    private Long wordCount;
    private Boolean inProgress;

    public boolean isTopped() {
        return this.topPriority != null && this.topPriority > 0;
    }

}
