package run.halo.app.model.params.cern.paper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.model.dto.base.InputConverter;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.model.params.cern.CernPostParam;

import java.util.Date;
import java.util.Set;

/**
 * paper param.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaperParam extends CernPostParam implements InputConverter<Paper> {
    private String publisher;
    private Date publishDate;
    private Set<Integer> authorIds;

    /**
     * Convert to domain.(shallow)
     *
     * @return new domain with same value(not null)
     */
    @Override
    public Paper convertTo() {
        checkFormat();
        Paper paper = InputConverter.super.convertTo();
        populateContent(paper);
        return paper;
    }

    /**
     * Update a domain by dto.(shallow)
     *
     * @param domain updated domain
     */
    @Override
    public void update(Paper domain) {
        checkFormat();
        populateContent(domain);
        InputConverter.super.update(domain);
    }
}
