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
public class NewsListIDTO implements OutputConverter<NewsListIDTO, News> {
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
    private String source;
    private String sourceLink;
    private String fullPath;
}
