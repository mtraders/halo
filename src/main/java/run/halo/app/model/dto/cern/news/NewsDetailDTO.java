package run.halo.app.model.dto.cern.news;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.lang.NonNull;
import run.halo.app.model.entity.Content.PatchedContent;
import run.halo.app.model.entity.cern.News;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class NewsDetailDTO extends NewsListDTO {
    private String originalContent;
    private String content;
    private Long commentCount = 0L;

    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends NewsListDTO> T convertFrom(@NonNull News news) {
        NewsDetailDTO newsDetailDTO = super.convertFrom(news);
        PatchedContent content = news.getContent();
        newsDetailDTO.setContent(content.getContent());
        newsDetailDTO.setOriginalContent(content.getOriginalContent());
        return (T) newsDetailDTO;
    }
}
