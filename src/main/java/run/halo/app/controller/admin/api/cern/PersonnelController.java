package run.halo.app.controller.admin.api.cern;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import run.halo.app.model.dto.cern.PersonnelDTO;

@RestController
@RequestMapping("/api/admin/cern/personnel")
public class PersonnelController {

    /**
     * list personnel.
     *
     * @param sort sort
     * @param more more information, with count or not
     * @return personnel list
     */
    @GetMapping
    @ApiOperation(value = "List Personnel")
    public List<? extends PersonnelDTO> listPersonnel(Sort sort, Boolean more) {
        return Collections.emptyList();
    }
}
