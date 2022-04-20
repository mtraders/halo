package run.halo.app.service.assembler.cern;


import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.stereotype.Component;
import run.halo.app.model.dto.cern.paper.PaperListDTO;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.model.vo.cern.paper.PaperListVO;
import run.halo.app.service.ContentService;
import run.halo.app.service.assembler.BasePostAssembler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * paper assembler.
 *
 * @author lizc
 */
@Component
public class PaperAssembler {

    private final ContentService contentService;

    public PaperAssembler(ContentService contentService) {
        super();
        this.contentService = contentService;
    }

    private PaperListVO convertToListVO(Paper paper) {
        Assert.notNull(paper, "Paper must not be null");
        PaperListDTO paperListDTO = new PaperListDTO().convertFrom(paper);
        return new PaperListVO();
    }

    public List<PaperListVO> convertToList(List<Paper> papers) {
        if (CollectionUtils.isEmpty(papers)) {
            return Collections.emptyList();
        }
        return papers.stream().map(this::convertToListVO).collect(Collectors.toList());
    }
}
