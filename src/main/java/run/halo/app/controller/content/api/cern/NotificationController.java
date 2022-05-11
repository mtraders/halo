package run.halo.app.controller.content.api.cern;

import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import run.halo.app.exception.NotFoundException;
import run.halo.app.model.entity.cern.Notification;
import run.halo.app.model.params.cern.notification.NotificationQuery;
import run.halo.app.model.vo.cern.notification.NotificationDetailVO;
import run.halo.app.model.vo.cern.notification.NotificationListVO;
import run.halo.app.service.PostService;
import run.halo.app.service.assembler.cern.NotificationAssembler;
import run.halo.app.service.cern.NotificationService;

import static org.springframework.data.domain.Sort.Direction.DESC;

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
    public NotificationController(NotificationService notificationService, NotificationAssembler notificationAssembler, PostService postService) {
        this.notificationService = notificationService;
        this.notificationAssembler = notificationAssembler;
        this.postService = postService;
    }

    /**
     * List notifications.
     *
     * @param pageable sort and page info.
     * @param keyword keyword.
     * @param categoryId category id.
     * @return notification list vo.
     */
    @GetMapping
    @ApiOperation("List notifications")
    public Page<NotificationListVO> pageBy(@PageableDefault(sort = {"topPriority", "createTime"}, direction = DESC) Pageable pageable,
                                           @RequestParam(value = "keyword", required = false) String keyword,
                                           @RequestParam(value = "categoryId", required = false) Integer categoryId) {
        NotificationQuery notificationQuery = new NotificationQuery();
        notificationQuery.setKeyword(keyword);
        notificationQuery.setCategoryId(categoryId);
        Page<Notification> notificationPage = notificationService.pageBy(notificationQuery, pageable);
        return notificationAssembler.convertToListVo(notificationPage);
    }

    /**
     * get notification detail.
     *
     * @param id notification id
     * @param formatDisabled format disable or not
     * @param sourceDisabled source disable or not
     * @return notification detail
     */
    @GetMapping("{id:\\d+}")
    @ApiOperation("get notification detail")
    public NotificationDetailVO getBy(@PathVariable("id") Integer id,
                                      @RequestParam(value = "formatDisabled", required = false, defaultValue = "true") Boolean formatDisabled,
                                      @RequestParam(value = "sourceDisabled", required = false, defaultValue = "false") Boolean sourceDisabled) {
        Notification notification = notificationService.getById(id);
        NotificationDetailVO detailVO = notificationAssembler.convertToDetailVo(notification);
        if (formatDisabled) {
            // clear the format content
            detailVO.setContent(null);
        }
        if (sourceDisabled) {
            // clear the original content
            detailVO.setOriginalContent(null);
        }
        postService.publishVisitEvent(detailVO.getId());
        return detailVO;
    }

    /**
     * get notification by slug.
     *
     * @param slug slug
     * @param formatDisabled format disable or not
     * @param sourceDisabled source disable or not
     * @return notification detail
     */
    @GetMapping("/slug")
    @ApiOperation("get notification by slug")
    public NotificationDetailVO getBy(@RequestParam("slug") String slug,
                                      @RequestParam(value = "formatDisabled", required = false, defaultValue = "true") Boolean formatDisabled,
                                      @RequestParam(value = "sourceDisabled", required = false, defaultValue = "false") Boolean sourceDisabled) {
        Notification notification = notificationService.getBySlug(slug);
        NotificationDetailVO detailVO = notificationAssembler.convertToDetailVo(notification);
        if (formatDisabled) {
            // clear the format content
            detailVO.setContent(null);
        }
        if (sourceDisabled) {
            // clear the original content
            detailVO.setOriginalContent(null);
        }
        postService.publishVisitEvent(detailVO.getId());
        return detailVO;
    }

    /**
     * get prev notification by current notification id.
     *
     * @param id id
     * @return notification detail.
     */
    @GetMapping("{id:\\d+}/prev")
    @ApiOperation("Get prev notification by current post id")
    public NotificationDetailVO getPrevNotificationBy(@PathVariable("id") Integer id) {
        Notification notification = notificationService.getById(id);
        Notification prevNotification = notificationService.getPrevPost(notification).orElseThrow(() -> new NotFoundException("查询不到该通知信息"));
        return notificationAssembler.convertToDetailVo(prevNotification);
    }

    /**
     * get next notification by current notification id.
     *
     * @param id id
     * @return notification detail.
     */
    @GetMapping("{id:\\d+}/next")
    @ApiOperation("Get next notification by current post id")
    public NotificationDetailVO getNextNotificationBy(@PathVariable("id") Integer id) {
        Notification notification = notificationService.getById(id);
        Notification prevNotification = notificationService.getNextPost(notification).orElseThrow(() -> new NotFoundException("查询不到该通知信息"));
        return notificationAssembler.convertToDetailVo(prevNotification);
    }
}
