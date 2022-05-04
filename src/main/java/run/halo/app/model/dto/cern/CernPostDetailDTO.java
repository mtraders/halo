package run.halo.app.model.dto.cern;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.NonNull;
import run.halo.app.model.entity.BasePost;
import run.halo.app.model.entity.Content;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class CernPostDetailDTO<POST extends BasePost> extends CernPostListDTO<POST> {
    private String originalContent;
    private String content;

    /**
     * convert from entity.
     *
     * @param cernPost cern post
     * @param <T> CernPostDetailDTO
     * @return cern detail dto.
     */
    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends CernPostListDTO<POST>> T convertFrom(@NonNull POST cernPost) {
        CernPostDetailDTO<POST> cernPostDetailDTO = super.convertFrom(cernPost);
        Content.PatchedContent content = cernPost.getContent();
        cernPostDetailDTO.setContent(content.getContent());
        cernPostDetailDTO.setOriginalContent(content.getOriginalContent());
        return (T) cernPostDetailDTO;
    }
}
