package run.halo.app.controller.admin.api;

import io.swagger.annotations.ApiOperation;
import org.apache.http.client.utils.URIBuilder;
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
import run.halo.app.cache.AbstractStringCacheStore;
import run.halo.app.model.dto.post.BasePostDetailDTO;
import run.halo.app.model.dto.post.BasePostMinimalDTO;
import run.halo.app.model.dto.post.BasePostSimpleDTO;
import run.halo.app.model.entity.Post;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.params.PostContentParam;
import run.halo.app.model.params.PostParam;
import run.halo.app.model.params.PostQuery;
import run.halo.app.model.vo.PostDetailVO;
import run.halo.app.service.OptionService;
import run.halo.app.service.PostService;
import run.halo.app.service.assembler.PostAssembler;
import run.halo.app.utils.HaloUtils;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Post controller.
 *
 * @author johnniang
 * @author ryanwang
 * @author guqing
 * @date 2019-03-19
 */
@RestController
@RequestMapping("/api/admin/posts")
public class PostController {

    private final PostService postService;

    private final AbstractStringCacheStore cacheStore;

    private final OptionService optionService;

    private final PostAssembler postAssembler;

    /**
     * post controller.
     *
     * @param postService post service.
     * @param cacheStore cache store.
     * @param optionService option service.
     * @param postAssembler post assembler.
     */
    public PostController(PostService postService, AbstractStringCacheStore cacheStore, OptionService optionService, PostAssembler postAssembler) {
        this.postService = postService;
        this.cacheStore = cacheStore;
        this.optionService = optionService;
        this.postAssembler = postAssembler;
    }

    /**
     * Lists posts.
     *
     * @param pageable page information.
     * @param postQuery post query information
     * @param more more
     * @return post list.
     */
    @GetMapping
    @ApiOperation("Lists posts")
    public Page<? extends BasePostSimpleDTO> pageBy(@PageableDefault(sort = {"topPriority", "createTime"}, direction = DESC) Pageable pageable,
                                                    PostQuery postQuery, @RequestParam(value = "more", defaultValue = "true") Boolean more) {
        Page<Post> postPage = postService.pageBy(postQuery, pageable);
        if (more) {
            return postAssembler.convertToListVo(postPage);
        }

        return postAssembler.convertToSimple(postPage);
    }

    @GetMapping("latest")
    @ApiOperation("Pages latest post")
    public List<BasePostMinimalDTO> pageLatest(@RequestParam(name = "top", defaultValue = "10") int top) {
        return postAssembler.convertToMinimal(postService.pageLatest(top).getContent());
    }

    /**
     * get a page of post by post status.
     *
     * @param status status.
     * @param more more information
     * @param pageable pageable information
     * @return post lists.
     */
    @GetMapping("status/{status}")
    @ApiOperation("Gets a page of post by post status")
    public Page<? extends BasePostSimpleDTO> pageByStatus(@PathVariable(name = "status") PostStatus status,
                                                          @RequestParam(value = "more", required = false, defaultValue = "false") Boolean more,
                                                          @PageableDefault(sort = "createTime", direction = DESC) Pageable pageable) {
        Page<Post> posts = postService.pageBy(status, pageable);

        if (more) {
            return postAssembler.convertToListVo(posts);
        }

        return postAssembler.convertToSimple(posts);
    }

    @GetMapping("{postId:\\d+}")
    @ApiOperation("Gets a post")
    public PostDetailVO getBy(@PathVariable("postId") Integer postId) {
        Post post = postService.getWithLatestContentById(postId);
        return postAssembler.convertToDetailVo(post);
    }

    @PutMapping("{postId:\\d+}/likes")
    @ApiOperation("Likes a post")
    public void likes(@PathVariable("postId") Integer postId) {
        postService.increaseLike(postId);
    }

    /**
     * post status.
     *
     * @param postParam post parameter.
     * @param autoSave auto save or not.
     * @return post detail.
     */
    @PostMapping
    @ApiOperation("Creates a post")
    public PostDetailVO createBy(@Valid @RequestBody PostParam postParam,
                                 @RequestParam(value = "autoSave", required = false, defaultValue = "false") Boolean autoSave) {
        // Convert to
        Post post = postParam.convertTo();
        return postService.createBy(post, postParam.getTagIds(), postParam.getCategoryIds(), postParam.getPostMetas(), autoSave);
    }

    /**
     * update the post.
     *
     * @param postParam post parameter.
     * @param postId post id.
     * @param autoSave auto save flag
     * @return post detail.
     */
    @PutMapping("{postId:\\d+}")
    @ApiOperation("Updates a post")
    public PostDetailVO updateBy(@Valid @RequestBody PostParam postParam, @PathVariable("postId") Integer postId,
                                 @RequestParam(value = "autoSave", required = false, defaultValue = "false") Boolean autoSave) {
        // Get the post info
        Post postToUpdate = postService.getWithLatestContentById(postId);

        postParam.update(postToUpdate);
        return postService.updateBy(postToUpdate, postParam.getTagIds(), postParam.getCategoryIds(), postParam.getPostMetas(), autoSave);
    }

    /**
     * update post status.
     *
     * @param postId post id.
     * @param status status.
     * @return post mini dto.
     */
    @PutMapping("{postId:\\d+}/status/{status}")
    @ApiOperation("Updates post status")
    public BasePostMinimalDTO updateStatusBy(@PathVariable("postId") Integer postId, @PathVariable("status") PostStatus status) {
        Post post = postService.updateStatus(status, postId);

        return new BasePostMinimalDTO().convertFrom(post);
    }

    @PutMapping("status/{status}")
    @ApiOperation("Updates post status in batch")
    public List<Post> updateStatusInBatch(@PathVariable(name = "status") PostStatus status, @RequestBody List<Integer> ids) {
        return postService.updateStatusByIds(ids, status);
    }

    /**
     * update draft post.
     *
     * @param postId post id.
     * @param contentParam content param.
     * @return base post detail dto.
     */
    @PutMapping("{postId:\\d+}/status/draft/content")
    @ApiOperation("Updates draft")
    public BasePostDetailDTO updateDraftBy(@PathVariable("postId") Integer postId, @RequestBody PostContentParam contentParam) {
        Post postToUse = postService.getById(postId);
        String formattedContent = contentParam.decideContentBy(postToUse.getEditorType());
        // Update draft content
        Post post = postService.updateDraftContent(formattedContent, contentParam.getOriginalContent(), postId);
        return postAssembler.convertToDetail(post);
    }

    @DeleteMapping("{postId:\\d+}")
    @ApiOperation("Deletes a post permanently")
    public void deletePermanently(@PathVariable("postId") Integer postId) {
        postService.removeById(postId);
    }

    @DeleteMapping
    @ApiOperation("Deletes posts permanently in batch by id array")
    public List<Post> deletePermanentlyInBatch(@RequestBody List<Integer> ids) {
        return postService.removeByIds(ids);
    }

    /**
     * gets a post preview link.
     *
     * @param postId post id.
     * @return post content.
     * @throws UnsupportedEncodingException unsupported encoding exception.
     * @throws URISyntaxException           url syntax exception.
     */
    @GetMapping(value = {"preview/{postId:\\d+}", "{postId:\\d+}/preview"})
    @ApiOperation("Gets a post preview link")
    public String preview(@PathVariable("postId") Integer postId) throws UnsupportedEncodingException, URISyntaxException {
        Post post = postService.getById(postId);

        post.setSlug(URLEncoder.encode(post.getSlug(), StandardCharsets.UTF_8.name()));

        BasePostMinimalDTO postMinimalDTO = postAssembler.convertToMinimal(post);

        String token = HaloUtils.simpleUUID();

        // cache preview token
        cacheStore.putAny(token, token, 10, TimeUnit.MINUTES);

        StringBuilder previewUrl = new StringBuilder();

        if (!optionService.isEnabledAbsolutePath()) {
            previewUrl.append(optionService.getBlogBaseUrl());
        }

        previewUrl.append(postMinimalDTO.getFullPath());

        // build preview post url and return
        return new URIBuilder(previewUrl.toString()).addParameter("token", token).build().toString();
    }
}
