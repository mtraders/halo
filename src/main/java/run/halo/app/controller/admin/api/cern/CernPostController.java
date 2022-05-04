package run.halo.app.controller.admin.api.cern;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import run.halo.app.model.dto.cern.CernPostListDTO;

/**
 * Cern post admin controller.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@RestController
@RequestMapping("/api/admin/cern/post")
public class CernPostController {
    public CernPostController() {
    }

    public Page<? extends CernPostListDTO<?>> pageBy() {
        return null;
    }
}
