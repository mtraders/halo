package run.halo.app.service.cern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import run.halo.app.model.entity.cern.Notification;
import run.halo.app.model.params.cern.CernPostQuery;
import run.halo.app.model.vo.cern.notification.NotificationDetailVO;
import run.halo.app.service.base.BasePostService;

import java.util.Set;

/**
 * Notification service.
 *
 * @author <a href="lizc@fists.cn">lizc</a>
 */
public interface NotificationService extends BasePostService<Notification> {

    /**
     * page notifications.
     *
     * @param notificationQuery notification query.
     * @param pageable page info.
     * @return notification page.
     */
    @NonNull
    Page<Notification> pageBy(@NonNull CernPostQuery<Notification> notificationQuery, @NonNull Pageable pageable);

    /**
     * create a notification.
     *
     * @param notification notification entity.
     * @param tagIds tag ids.
     * @param categoryIds category ids.
     * @param autoSave auto-save or not
     * @return notification detail vo.
     */
    @NonNull
    NotificationDetailVO createBy(@NonNull Notification notification, Set<Integer> tagIds, Set<Integer> categoryIds, boolean autoSave);


    /**
     * Update a notification.
     *
     * @param notification notification to update
     * @param tagIds tag ids
     * @param categoryIds category ids
     * @param autoSave auto save flag.
     * @return notification detail vo.
     */
    @NonNull
    NotificationDetailVO updateBy(@NonNull Notification notification, Set<Integer> tagIds, Set<Integer> categoryIds, boolean autoSave);
}
