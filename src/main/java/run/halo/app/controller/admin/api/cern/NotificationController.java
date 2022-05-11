package run.halo.app.controller.admin.api.cern;

import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import run.halo.app.model.dto.cern.notification.NotificationListDTO;
import run.halo.app.model.entity.cern.Notification;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.params.PostContentParam;
import run.halo.app.model.params.cern.notification.NotificationParam;
import run.halo.app.model.params.cern.notification.NotificationQuery;
import run.halo.app.model.vo.cern.notification.NotificationDetailVO;
import run.halo.app.model.vo.cern.notification.NotificationListVO;
import run.halo.app.service.assembler.cern.NotificationAssembler;
import run.halo.app.service.cern.NotificationService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * notification admin controller.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@RestController("ApiAdminCernNotificationController")
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
     * @param notificationQuery notification query info.
     * @param more more info or not
     * @return notification list data.
     */
    @GetMapping
    @ApiOperation(value = "get notification list")
    public Page<? extends NotificationListDTO> pageBy(@PageableDefault(sort = {"topPriority", "createTime"}, direction = DESC) Pageable pageable,
                                                      NotificationQuery notificationQuery,
                                                      @RequestParam(value = "more", defaultValue = "true") Boolean more) {
        Page<Notification> pageData = notificationService.pageBy(notificationQuery, pageable);
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

    /**
     * Update notification.
     *
     * @param notificationId notification id
     * @param notificationParam notification param
     * @param autoSave autoSave flag.
     * @return notification detail vo.
     */
    @PutMapping("{id:\\d+}")
    @ApiOperation("Update a notification")
    public NotificationDetailVO updateBy(@PathVariable("id") Integer notificationId, @RequestBody @Valid NotificationParam notificationParam,
                                         @RequestParam(value = "autoSave", required = false, defaultValue = "false") Boolean autoSave) {
        Notification notificationToUpdate = notificationService.getWithLatestContentById(notificationId);
        notificationParam.update(notificationToUpdate);
        Set<Integer> tagIds = notificationParam.getTagIds();
        Set<Integer> categoryIds = notificationParam.getCategoryIds();
        return notificationService.updateBy(notificationToUpdate, tagIds, categoryIds, autoSave);
    }

    /**
     * Update notification status.
     *
     * @param notificationId notification id.
     * @param status status.
     * @return notification list vo.
     */
    @PutMapping("{id:\\d+}/{status}")
    @ApiOperation("Update notification status")
    public NotificationListVO updateStatusBy(@PathVariable("id") Integer notificationId, @PathVariable("status") PostStatus status) {
        Notification notification = notificationService.updateStatus(status, notificationId);
        return notificationAssembler.convertToListVo(notification);
    }

    /**
     * Update draft notification.
     *
     * @param notificationId notification id.
     * @param contentParam content param.
     * @return notification detail vo.
     */
    @PutMapping("{id:\\d+}/status/draft/content")
    @ApiOperation("Update draft notification")
    public NotificationDetailVO updateDraftBy(@PathVariable("id") Integer notificationId, @RequestBody PostContentParam contentParam) {
        Notification notificationToUse = notificationService.getById(notificationId);
        String formattedContent = contentParam.decideContentBy(notificationToUse.getEditorType());
        Notification notification = notificationService.updateDraftContent(formattedContent, contentParam.getOriginalContent(), notificationId);
        return notificationAssembler.convertToDetailVo(notification);
    }

    /**
     * Delete a notification permanently.
     *
     * @param notificationId notification id.
     * @return notification entity.
     */
    @DeleteMapping("{id:\\d+}")
    @ApiOperation("Delete a notification permanently.")
    public Notification deletePermanently(@PathVariable("id") Integer notificationId) {
        return notificationService.removeById(notificationId);
    }

    @DeleteMapping
    @ApiOperation("Deletes notifications permanently in batch by id array")
    public List<Notification> deletePermanentlyInBatch(@RequestBody List<Integer> ids) {
        return notificationService.removeByIds(ids);
    }

    /**
     * get a notification preview.
     *
     * @param notificationId notification id.
     * @return preview content.
     */
    @GetMapping("preview/{id:\\d+}")
    @ApiOperation("Get a notification preview")
    public String preview(@PathVariable("id") Integer notificationId) {
        return notificationId.toString();
    }
}
