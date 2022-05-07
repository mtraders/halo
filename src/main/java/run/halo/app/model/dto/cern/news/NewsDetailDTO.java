package run.halo.app.model.dto.cern.news;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.lang.NonNull;
import run.halo.app.model.dto.cern.CernPostListDTO;
import run.halo.app.model.entity.Content;
import run.halo.app.model.entity.cern.News;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NewsDetailDTO extends NewsListDTO {
    private String originalContent;
    private String content;

    /**
     * Convert from domain.(shallow)
     *
     * @param news domain data
     * @return converted dto data
     */
    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends CernPostListDTO<News>> T convertFrom(@NonNull News news) {
        NewsDetailDTO newsDetailDTO = super.convertFrom(news);
        Content.PatchedContent content = news.getContent();
        newsDetailDTO.setContent(content.getContent());
        newsDetailDTO.setOriginalContent(content.getOriginalContent());
        return (T) newsDetailDTO;
    }
}
