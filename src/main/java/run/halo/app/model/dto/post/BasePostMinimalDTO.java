package run.halo.app.model.dto.post;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.model.dto.base.OutputConverter;
import run.halo.app.model.entity.BasePost;
import run.halo.app.model.enums.PostEditorType;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.enums.cern.PostType;

import java.util.Date;

/**
 * Base post minimal output dto.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-19
 */
@Data
@ToString
@EqualsAndHashCode
public class BasePostMinimalDTO implements OutputConverter<BasePostMinimalDTO, BasePost> {

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

    // cern fields
    private PostType postType;
    private String postSource;
    private String postSourceLink;
    private String projectPeriod;
    private String projectSource;
    private String projectManager;
    private Date paperPublishDate;
    private String paperPublisher;
    private String paperAuthors;
}
