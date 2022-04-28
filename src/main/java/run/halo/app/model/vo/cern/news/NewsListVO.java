package run.halo.app.model.vo.cern.news;

import lombok.Data;
import run.halo.app.model.dto.base.OutputConverter;
import run.halo.app.model.dto.cern.news.NewsDTO;
import run.halo.app.model.entity.cern.Personnel;
import run.halo.app.model.enums.PostEditorType;
import run.halo.app.model.enums.PostStatus;

import java.util.Date;

/**
 * News list vo.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
public class NewsListVO implements OutputConverter<NewsListVO, NewsDTO> {
    private Integer id;
    private String title;
    private PostStatus status;
    private String slug;
    private PostEditorType editorType;
    private Date updateTime;
    private Date editTime;
    private String metaKeywords;
    private String metaDescription;
    private String fullPath;
    private Personnel author;
}
