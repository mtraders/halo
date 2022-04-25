package run.halo.app.controller.admin.api.cern;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import run.halo.app.model.vo.cern.news.NewsListVO;

/**
 * News controller.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@RestController
@RequestMapping("/api/admin/cern/news")
public class NewsController {

    public NewsController() {}

    public Page<NewsListVO> pageBy() {
        return null;
    }

}
