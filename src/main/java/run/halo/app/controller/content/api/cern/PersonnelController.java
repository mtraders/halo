package run.halo.app.controller.content.api.cern;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import run.halo.app.model.dto.cern.personnel.PersonnelDTO;
import run.halo.app.service.cern.PersonnelService;
import run.halo.app.service.cern.PostPersonnelService;

import java.util.Collections;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Cern Personnel Controller.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@RestController("ApiContentCernPersonnelController")
@RequestMapping("/api/content/cern/personnel")
public class PersonnelController {
    private final PersonnelService personnelService;

    private final PostPersonnelService postPersonnelService;

    /**
     * constructor of personnel content controller.
     *
     * @param personnelService personnel service.
     * @param postPersonnelService post personnel service.
     */
    public PersonnelController(PersonnelService personnelService, PostPersonnelService postPersonnelService) {
        this.personnelService = personnelService;
        this.postPersonnelService = postPersonnelService;
    }

    /**
     * list personnel.
     *
     * @param sort sort information
     * @param more more info with count
     * @return personnel list.
     */
    @GetMapping
    @ApiOperation("List personnel")
    public List<? extends PersonnelDTO> listPersonnel(@SortDefault(sort = "updateTime", direction = DESC) Sort sort,
                                                      @ApiParam("If the param is true, post count of tag will be returned")
                                                      @RequestParam(name = "more", required = false, defaultValue = "false") Boolean more) {
        if (more) {
            return postPersonnelService.listPersonnelMore(sort);
        }
        return personnelService.convertTo(personnelService.listAll(sort));
    }
}
