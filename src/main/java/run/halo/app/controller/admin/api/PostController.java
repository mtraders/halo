package run.halo.app.controller.admin.api;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
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
import run.halo.app.exception.AuthenticationException;
import run.halo.app.model.dto.post.BasePostDetailDTO;
import run.halo.app.model.dto.post.BasePostMinimalDTO;
import run.halo.app.model.dto.post.BasePostSimpleDTO;
import run.halo.app.model.entity.Post;
import run.halo.app.model.entity.User;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.enums.UserType;
import run.halo.app.model.params.PostContentParam;
import run.halo.app.model.params.PostParam;
import run.halo.app.model.params.PostQuery;
import run.halo.app.model.vo.PostDetailVO;
import run.halo.app.service.OptionService;
import run.halo.app.service.PostService;
import run.halo.app.service.assembler.PostAssembler;
import run.halo.app.service.cern.PostUserService;
import run.halo.app.utils.HaloUtils;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Post controller.
 *
 * @author johnniang
 * @author ryanwang
 * @author guqing
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 * @date 2019-03-19
 */
@RestController
@RequestMapping("/api/admin/posts")
@Slf4j
public class PostController {

    private final PostService postService;

    private final AbstractStringCacheStore cacheStore;

    private final OptionService optionService;

    private final PostAssembler postAssembler;

    private final PostUserService postUserService;

    /**
     * post controller.
     *
     * @param postService post service.
     * @param cacheStore cache store.
     * @param optionService option service.
     * @param postAssembler post assembler.
     * @param postUserService post user service.
     */
    public PostController(PostService postService, AbstractStringCacheStore cacheStore, OptionService optionService, PostAssembler postAssembler,
                          PostUserService postUserService) {
        this.postService = postService;
        this.cacheStore = cacheStore;
        this.optionService = optionService;
        this.postAssembler = postAssembler;
        this.postUserService = postUserService;
    }

    /**
     * Lists posts.
     *
     * @param pageable page information.
     * @param postQuery post query information
     * @param more more
     * @param user user information(passed from authentication filter)
     * @return post list.
     */
    @GetMapping
    @ApiOperation("Lists posts")
    public Page<? extends BasePostSimpleDTO> pageBy(@PageableDefault(sort = {"topPriority", "createTime"}, direction = DESC) Pageable pageable,
                                                    PostQuery postQuery, @RequestParam(value = "more", defaultValue = "true") Boolean more,
                                                    User user) {
        // if user is not admin, show only posts created by current user.
        Assert.notNull(user, "User must not be null");
        if (user.getUserType() != UserType.ADMIN) {
            postQuery.setUserId(user.getId());
        }
        Page<Post> postPage = postService.pageBy(postQuery, pageable);
        if (more) {
            return postAssembler.convertToListVo(postPage);
        }

        return postAssembler.convertToSimple(postPage);
    }

    /**
     * pages latest post.
     *
     * @param top post count.
     * @param user user info passed from auth filter.
     * @return latest posts
     */
    @GetMapping("latest")
    @ApiOperation("Pages latest post")
    public List<BasePostMinimalDTO> pageLatest(@RequestParam(name = "top", defaultValue = "10") int top, User user) {
        // if user is not admin, show only posts created by current user.
        Assert.notNull(user, "User must not be null");
        PostQuery postQuery = new PostQuery();
        if (user.getUserType() != UserType.ADMIN) {
            postQuery.setUserId(user.getId());
        } else {
            postQuery = null;
        }
        return postAssembler.convertToMinimal(postService.pageLatestByQueryInfo(top, postQuery).getContent());
    }

    /**
     * get a page of post by post status.
     *
     * @param status status.
     * @param more more information
     * @param pageable pageable information
     * @param user user info passed from auth filter.
     * @return post lists.
     */
    @GetMapping("status/{status}")
    @ApiOperation("Gets a page of post by post status")
    public Page<? extends BasePostSimpleDTO> pageByStatus(@PathVariable(name = "status") PostStatus status,
                                                          @RequestParam(value = "more", required = false, defaultValue = "false") Boolean more,
                                                          @PageableDefault(sort = "createTime", direction = DESC) Pageable pageable, User user) {
        // if user is not admin, show only posts created by current user.
        Assert.notNull(user, "User must not be null");
        Page<Post> posts;
        if (user.getUserType() == UserType.ADMIN) {
            posts = postService.pageBy(status, pageable);
        } else {
            PostQuery postQuery = new PostQuery();
            postQuery.setStatuses(Set.of(status));
            postQuery.setUserId(user.getId());
            posts = postService.pageBy(postQuery, pageable);
        }
        if (more) {
            return postAssembler.convertToListVo(posts);
        }
        return postAssembler.convertToSimple(posts);
    }

    /**
     * get a post by id.
     *
     * @param postId post id
     * @param user user info passed from auth filter.
     * @return post detail.
     */
    @GetMapping("{postId:\\d+}")
    @ApiOperation("Gets a post")
    public PostDetailVO getBy(@PathVariable("postId") Integer postId, User user) {
        Assert.notNull(user, "User must not be null");
        if (user.getUserType() != UserType.ADMIN && !postUserService.exists(postId, user.getId())) {
            throw new AuthenticationException("无权访问该文章信息");
        }
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
     * @param user user info passed from auth filter.
     * @return post detail.
     */
    @PostMapping
    @ApiOperation("Creates a post")
    public PostDetailVO createBy(@Valid @RequestBody PostParam postParam,
                                 @RequestParam(value = "autoSave", required = false, defaultValue = "false") Boolean autoSave, User user) {
        Assert.notNull(user, "User must not be null");
        // Convert to
        Post post = postParam.convertTo();
        // cern auditing support
        if (post.getStatus() == PostStatus.PUBLISHED && user.getUserType() != UserType.ADMIN) {
            post.setStatus(PostStatus.AUDITING);
        }
        Set<Integer> userIds = postParam.getUserIds();
        userIds.add(user.getId());
        return postService.createBy(post, postParam.getTagIds(), postParam.getCategoryIds(), postParam.getPostMetas(), userIds, autoSave);
    }

    /**
     * update the post.
     *
     * @param postParam post parameter.
     * @param postId post id.
     * @param autoSave auto save flag
     * @param user user info passed from auth filter.
     * @return post detail.
     */
    @PutMapping("{postId:\\d+}")
    @ApiOperation("Updates a post")
    public PostDetailVO updateBy(@Valid @RequestBody PostParam postParam, @PathVariable("postId") Integer postId,
                                 @RequestParam(value = "autoSave", required = false, defaultValue = "false") Boolean autoSave, User user) {
        Assert.notNull(user, "User must not be null");
        if (user.getUserType() != UserType.ADMIN && !postUserService.exists(postId, user.getId())) {
            throw new AuthenticationException("无权修改该文章");
        }
        // Get the post info
        Post postToUpdate = postService.getWithLatestContentById(postId);
        Set<Integer> userIds = postParam.getUserIds();
        userIds.add(user.getId());
        postParam.update(postToUpdate);
        // cern auditing support
        if (postToUpdate.getStatus() == PostStatus.PUBLISHED && user.getUserType() != UserType.ADMIN) {
            postToUpdate.setStatus(PostStatus.AUDITING);
        }
        return postService.updateBy(postToUpdate, postParam.getTagIds(), postParam.getCategoryIds(), postParam.getPostMetas(), userIds, autoSave);
    }

    /**
     * update post status.
     *
     * @param postId post id.
     * @param status status.
     * @param user user info passed from auth filter.
     * @return post mini dto.
     */
    @PutMapping("{postId:\\d+}/status/{status}")
    @ApiOperation("Updates post status")
    public BasePostMinimalDTO updateStatusBy(@PathVariable("postId") Integer postId, @PathVariable("status") PostStatus status, User user) {
        Assert.notNull(user, "User must not be null");
        if (user.getUserType() != UserType.ADMIN && !postUserService.exists(postId, user.getId())) {
            throw new AuthenticationException("无权修改该文章状态");
        }
        Post post = postService.updateStatus(status, postId);
        return new BasePostMinimalDTO().convertFrom(post);
    }

    /**
     * update status in batch.
     *
     * @param status status.
     * @param ids post ids
     * @param user user info passed from auth filter.
     * @return post list
     */
    @PutMapping("status/{status}")
    @ApiOperation("Updates post status in batch")
    public List<Post> updateStatusInBatch(@PathVariable(name = "status") PostStatus status, @RequestBody List<Integer> ids, User user) {
        Assert.notNull(user, "User must not be null");
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        if (user.getUserType() != UserType.ADMIN) {
            if (status == PostStatus.PUBLISHED) {
                throw new AuthenticationException("无权将文章状态置为发布状态");
            }
            ids.removeIf(postId -> {
                boolean exists = postUserService.exists(postId, user.getId());
                if (!exists) {
                    log.warn("Post id {} is not your post, you can not update its status.", postId);
                }
                return !exists;
            });
        }
        return postService.updateStatusByIds(ids, status);
    }

    /**
     * update draft post.
     *
     * @param postId post id.
     * @param contentParam content param.
     * @param user user info passed from auth filter.
     * @return base post detail dto.
     */
    @PutMapping("{postId:\\d+}/status/draft/content")
    @ApiOperation("Updates draft")
    public BasePostDetailDTO updateDraftBy(@PathVariable("postId") Integer postId, @RequestBody PostContentParam contentParam, User user) {
        Assert.notNull(user, "User must not be null");
        if (user.getUserType() != UserType.ADMIN && !postUserService.exists(postId, user.getId())) {
            throw new AuthenticationException("无权修改该文章");
        }

        Post postToUse = postService.getById(postId);
        String formattedContent = contentParam.decideContentBy(postToUse.getEditorType());
        // Update draft content
        Post post = postService.updateDraftContent(formattedContent, contentParam.getOriginalContent(), postId);
        return postAssembler.convertToDetail(post);
    }

    /**
     * delete post permanently.
     *
     * @param postId post id.
     * @param user user info passed from auth filter.
     */
    @DeleteMapping("{postId:\\d+}")
    @ApiOperation("Delete a post permanently")
    public void deletePermanently(@PathVariable("postId") Integer postId, User user) {
        Assert.notNull(user, "User must not be null");
        if (user.getUserType() != UserType.ADMIN && !postUserService.exists(postId, user.getId())) {
            throw new AuthenticationException("无权删除文章");
        }
        postService.removeById(postId);
    }

    /**
     * delete posts permanently in batch by id array.
     *
     * @param ids post ids.
     * @param user user info passed from auth filter.
     * @return deleted post
     */
    @DeleteMapping
    @ApiOperation("Delete posts permanently in batch by id array")
    public List<Post> deletePermanentlyInBatch(@RequestBody List<Integer> ids, User user) {
        Assert.notNull(user, "User must not be null");
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        if (user.getUserType() != UserType.ADMIN) {
            ids.removeIf(postId -> {
                boolean exists = postUserService.exists(postId, user.getId());
                if (!exists) {
                    log.warn("Post id {} is not your post, you can not delete it.", postId);
                }
                return !exists;
            });
        }
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
