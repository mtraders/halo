package run.halo.app.event.cern;

import org.springframework.context.ApplicationEvent;
import run.halo.app.model.entity.cern.Notification;

/**
 * Notification update event.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
public class NotificationUpdateEvent extends ApplicationEvent {
    private final Notification notification;

    public NotificationUpdateEvent(Object source, Notification notification) {
        super(source);
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }
}
