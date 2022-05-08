package run.halo.app.controller.admin.api.cern;

import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import run.halo.app.model.dto.cern.notification.NotificationListDTO;
import run.halo.app.model.entity.cern.Notification;
import run.halo.app.model.params.cern.CernPostQuery;
import run.halo.app.model.params.cern.NotificationParam;
import run.halo.app.model.vo.cern.notification.NotificationDetailVO;
import run.halo.app.service.assembler.cern.NotificationAssembler;
import run.halo.app.service.cern.NotificationService;

import javax.validation.Valid;
import java.util.Set;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * notification admin controller.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@RestController
@RequestMapping("/api/admin/cern/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationAssembler notificationAssembler;

    /**
     * constructor of notification admin controller.
     *
     * @param notificationService notification service.
     * @param notificationAssembler notification assembler service.
     */
    public NotificationController(NotificationService notificationService, NotificationAssembler notificationAssembler) {
        this.notificationService = notificationService;
        this.notificationAssembler = notificationAssembler;
    }

    /**
     * get notification list.
     *
     * @param pageable page info.
     * @param cernPostQuery cern post query info.
     * @param more more info or not
     * @return notification list data.
     */
    @GetMapping
    @ApiOperation(value = "get notification list")
    public Page<? extends NotificationListDTO> pageBy(@PageableDefault(sort = {"topPriority", "createTime"}, direction = DESC) Pageable pageable,
                                                      CernPostQuery<Notification> cernPostQuery,
                                                      @RequestParam(value = "more", defaultValue = "true") Boolean more) {
        Page<Notification> pageData = notificationService.pageBy(cernPostQuery, pageable);
        if (more) {
            return notificationAssembler.convertToListVo(pageData);
        }
        return notificationAssembler.convertToListDTO(pageData);
    }

    /**
     * create a notification.
     *
     * @param notificationParam notification param.
     * @param autoSave auto-save flag.
     * @return notification detail vo.
     */
    @PostMapping
    @ApiOperation("create a notification")
    public NotificationDetailVO createBy(@RequestBody @Valid NotificationParam notificationParam,
                                         @RequestParam(value = "autoSave", required = false, defaultValue = "false") Boolean autoSave) {
        Notification notification = notificationParam.convertTo();
        Set<Integer> tagIds = notificationParam.getTagIds();
        Set<Integer> categoryIds = notificationParam.getCategoryIds();
        return notificationService.createBy(notification, tagIds, categoryIds, autoSave);
    }

    /**
     * get a notification.
     *
     * @param notificationId id param
     * @return notification detail vo.
     */
    @GetMapping("{id:\\d+}")
    @ApiOperation("Get a notification")
    public NotificationDetailVO getBy(@PathVariable("id") Integer notificationId) {
        Notification notification = notificationService.getWithLatestContentById(notificationId);
        return notificationAssembler.convertToDetailVo(notification);
    }

}
