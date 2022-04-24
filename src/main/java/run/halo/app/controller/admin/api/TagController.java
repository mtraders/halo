package run.halo.app.controller.admin.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
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
import run.halo.app.model.dto.TagDTO;
import run.halo.app.model.entity.Tag;
import run.halo.app.model.params.TagParam;
import run.halo.app.service.PostTagService;
import run.halo.app.service.TagService;

import javax.validation.Valid;
import java.util.List;

/**
 * Tag controller.
 *
 * @author johnniang
 * @date 3/20/19
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/tags")
public class TagController {

    private final TagService tagService;

    private final PostTagService postTagService;

    public TagController(TagService tagService, PostTagService postTagService) {
        this.tagService = tagService;
        this.postTagService = postTagService;
    }

    /**
     * list tags.
     *
     * @param sort sort order
     * @param more more
     * @return tag list.
     */
    @GetMapping
    @ApiOperation("Lists tags")
    public List<? extends TagDTO> listTags(@SortDefault(sort = "createTime", direction = Sort.Direction.DESC) Sort sort,
                                           @ApiParam("Return more information(post count) if it is set")
                                           @RequestParam(name = "more", required = false, defaultValue = "false") Boolean more) {
        if (more) {
            return postTagService.listTagWithCountDtos(sort);
        }
        return tagService.convertTo(tagService.listAll(sort));
    }

    /**
     * create a tag.
     *
     * @param tagParam tag param
     * @return tag
     */
    @PostMapping
    @ApiOperation("Create a tag")
    public TagDTO createTag(@Valid @RequestBody TagParam tagParam) {
        // Convert to tag
        Tag tag = tagParam.convertTo();

        log.debug("Tag to be created: [{}]", tag);

        // Create and convert
        return tagService.convertTo(tagService.create(tag));
    }

    /**
     * get tag detail by id.
     *
     * @param tagId tag id.
     * @return tag detail
     */
    @GetMapping("{tagId:\\d+}")
    @ApiOperation("Get tag detail by id")
    public TagDTO getBy(@PathVariable("tagId") Integer tagId) {
        return tagService.convertTo(tagService.getById(tagId));
    }

    /**
     * update tag.
     *
     * @param tagId tag id
     * @param tagParam tag parameter
     * @return tag detail
     */
    @PutMapping("{tagId:\\d+}")
    @ApiOperation("Update a tag")
    public TagDTO updateBy(@PathVariable("tagId") Integer tagId, @Valid @RequestBody TagParam tagParam) {
        // Get old tag
        Tag tag = tagService.getById(tagId);

        // Update tag
        tagParam.update(tag);

        // Update tag
        return tagService.convertTo(tagService.update(tag));
    }

    /**
     * delete tag.
     *
     * @param tagId tag id
     * @return tag detail
     */
    @DeleteMapping("{tagId:\\d+}")
    @ApiOperation("Deletes a tag")
    public TagDTO deletePermanently(@PathVariable("tagId") Integer tagId) {
        // Remove the tag
        Tag deletedTag = tagService.removeById(tagId);
        // Remove the post tag relationship
        postTagService.removeByTagId(tagId);

        return tagService.convertTo(deletedTag);
    }
}
