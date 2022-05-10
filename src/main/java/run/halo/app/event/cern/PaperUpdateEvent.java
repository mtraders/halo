package run.halo.app.event.cern;

import org.springframework.context.ApplicationEvent;
import run.halo.app.model.entity.cern.Paper;

/**
 * Paper update event.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
public class PaperUpdateEvent extends ApplicationEvent {
    private final Paper paper;

    public PaperUpdateEvent(Object source, Paper paper) {
        super(source);
        this.paper = paper;
    }

    public Paper getPaper() {
        return paper;
    }
}
