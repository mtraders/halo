package run.halo.app.model.dto.cern.paper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.lang.NonNull;
import run.halo.app.model.dto.base.OutputConverter;
import run.halo.app.model.dto.cern.CernPostListDTO;
import run.halo.app.model.entity.Content;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.model.enums.PostEditorType;
import run.halo.app.model.enums.PostStatus;

import java.util.Date;

/**
 * paper detail dto.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class PaperDetailDTO extends PaperListDTO {
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
    public <T extends CernPostListDTO<Paper>> T convertFrom(@NonNull Paper domain) {
        PaperDetailDTO detailDTO = super.convertFrom(domain);
        Content.PatchedContent content = domain.getContent();
        detailDTO.setContent(content.getContent());
        detailDTO.setOriginalContent(content.getOriginalContent());
        return (T) detailDTO;
    }
}
