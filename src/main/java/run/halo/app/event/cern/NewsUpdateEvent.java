package run.halo.app.event.cern;

import org.springframework.context.ApplicationEvent;
import run.halo.app.model.entity.cern.News;

/**
 * news update event.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
public class NewsUpdateEvent extends ApplicationEvent {
    private final News news;

    public NewsUpdateEvent(Object source, News news) {
        super(source);
        this.news = news;
    }

    public News getNews() {
        return news;
    }
}
