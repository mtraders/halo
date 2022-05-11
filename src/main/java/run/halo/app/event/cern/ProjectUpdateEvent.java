package run.halo.app.event.cern;

import org.springframework.context.ApplicationEvent;
import run.halo.app.model.entity.cern.Project;

/**
 * Project update event.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
public class ProjectUpdateEvent extends ApplicationEvent {
    private final Project project;

    public ProjectUpdateEvent(Object source, Project project) {
        super(source);
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}
