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
     * @param domain domain data
     * @return converted dto data
     */
    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends CernPostListDTO<News>> T convertFrom(@NonNull News domain) {
        NewsDetailDTO detailDTO = super.convertFrom(domain);
        Content.PatchedContent content = domain.getContent();
        detailDTO.setContent(content.getContent());
        detailDTO.setOriginalContent(content.getOriginalContent());
        return (T) detailDTO;
    }
}
