package run.halo.app.model.vo.cern.personnel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.model.vo.cern.news.NewsListVO;
import run.halo.app.model.vo.cern.notification.NotificationListVO;
import run.halo.app.model.vo.cern.paper.PaperListVO;
import run.halo.app.model.vo.cern.project.ProjectListVO;

import java.util.List;

/**
 * personnel detail vo.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonnelDetailVO extends PersonnelListVO {
    private List<NewsListVO> newses;
    private List<NotificationListVO> notifications;
    private List<PaperListVO> papers;
    private List<ProjectListVO> projects;
}
