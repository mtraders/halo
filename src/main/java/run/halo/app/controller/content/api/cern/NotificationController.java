package run.halo.app.controller.content.api.cern;

import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import run.halo.app.model.vo.cern.notification.NotificationDetailVO;
import run.halo.app.model.vo.cern.notification.NotificationListVO;
import run.halo.app.service.PostService;
import run.halo.app.service.assembler.cern.NotificationAssembler;
import run.halo.app.service.cern.NotificationService;

/**
 * Notification content controller.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@RestController("ApiContentCernNotificationController")
@RequestMapping(value = "/api/content/cern/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationAssembler notificationAssembler;
    private final PostService postService;

    /**
     * constructor of notification controller.
     *
     * @param notificationService notification service.
     * @param notificationAssembler notification assembler.
     * @param postService post service.
     */
    public NotificationController(NotificationService notificationService,
                                  NotificationAssembler notificationAssembler, PostService postService) {
        this.notificationService = notificationService;
        this.notificationAssembler = notificationAssembler;
        this.postService = postService;
    }

    @GetMapping
    @ApiOperation("List notifications")
    public Page<NotificationListVO> pageBy() {
        return null;
    }

    public NotificationDetailVO getBy(Integer id) {
        return null;
    }

    public NotificationDetailVO getBy(String slug) {
        return null;
    }

    public NotificationDetailVO getPrevNotificationBy(Integer id) {
        return null;
    }

    public NotificationDetailVO getNextNotificationBy(Integer id) {
        return null;
    }
}
