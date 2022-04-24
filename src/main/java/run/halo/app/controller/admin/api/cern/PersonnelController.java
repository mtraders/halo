package run.halo.app.controller.admin.api.cern;

import java.util.Collections;
import java.util.List;

import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import run.halo.app.model.dto.cern.PersonnelDTO;
import run.halo.app.model.params.cern.PersonnelParam;
import run.halo.app.service.cern.PersonnelService;

import javax.validation.Valid;

/**
 * Personnel controller.
 *
 * @author lizc mailto:lizc@fists.cn
 */
@RestController
@RequestMapping("/api/admin/cern/personnel")
public class PersonnelController {

    private final PersonnelService personnelService;

    public PersonnelController(PersonnelService personnelService) {
        this.personnelService = personnelService;
    }

    /**
     * list personnel.
     *
     * @param sort sort
     * @param more more information, with count or not
     * @return personnel list
     */
    @GetMapping
    @ApiOperation(value = "List Personnel")
    public List<? extends PersonnelDTO> listPersonnel(@SortDefault(sort = "createTime", direction = Sort.Direction.DESC) Sort sort,
                                            @ApiParam("Return more information if it is set")
                                            @RequestParam(name = "more", required = false, defaultValue = "false") Boolean more) {
        return Collections.emptyList();
    }

    /**
     * crete personnel.
     *
     * @param personnelParam personnel param.
     * @return personnel dto.
     */
    @PostMapping
    @ApiOperation(value = "create personnel")
    public PersonnelDTO createPersonnel(@Valid @RequestBody PersonnelParam personnelParam) {
        return new PersonnelDTO();
    }

    /**
     * get personnel detail by id.
     *
     * @param personnelId personnel id
     * @return personnel detail.
     */
    @GetMapping("{personnelId:\\d+}")
    @ApiOperation("Get personnel detail by id")
    public PersonnelDTO getBy(@PathVariable("personnelId") Integer personnelId) {
        return new PersonnelDTO();
    }

    /**
     * update personnel by id.
     *
     * @param personnelId personnel id.
     * @param personnelParam personnel param
     * @return personnel detail.
     */
    @PutMapping("{personnelId:\\d+}")
    @ApiOperation("Update a person")
    public PersonnelDTO updateBy(@PathVariable("personnelId") Integer personnelId, @Valid @RequestBody PersonnelParam personnelParam) {
        return new PersonnelDTO();
    }

    /**
     * delete personnel permanently.
     *
     * @param personnelId personnel id.
     * @return deleted person detail.
     */
    @DeleteMapping("{personnelId:\\d+}")
    @ApiOperation("Delete person permanently.")
    public PersonnelDTO deletePermanently(@PathVariable("personnelId") Integer personnelId) {
        return new PersonnelDTO();
    }
}
