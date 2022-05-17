package run.halo.app.controller.content.api;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import run.halo.app.cache.AbstractStringCacheStore;
import run.halo.app.cache.lock.CacheLock;
import run.halo.app.cache.lock.CacheParam;
import run.halo.app.controller.content.auth.PostAuthentication;
import run.halo.app.exception.ForbiddenException;
import run.halo.app.exception.NotFoundException;
import run.halo.app.model.dto.BaseCommentDTO;
import run.halo.app.model.dto.post.BasePostSimpleDTO;
import run.halo.app.model.entity.Post;
import run.halo.app.model.entity.PostComment;
import run.halo.app.model.enums.CommentStatus;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.params.PostCommentParam;
import run.halo.app.model.params.PostQuery;
import run.halo.app.model.vo.BaseCommentVO;
import run.halo.app.model.vo.BaseCommentWithParentVO;
import run.halo.app.model.vo.CommentWithHasChildrenVO;
import run.halo.app.model.vo.PostDetailVO;
import run.halo.app.model.vo.PostListVO;
import run.halo.app.service.OptionService;
import run.halo.app.service.PostCommentService;
import run.halo.app.service.PostService;
import run.halo.app.service.assembler.PostRenderAssembler;
import run.halo.app.service.assembler.comment.PostCommentRenderAssembler;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Content post controller.
 *
 * @author johnniang
 * @author ryanwang
 * @author guqing
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 * @date 2019-04-02
 */
@RestController("ApiContentPostController")
@RequestMapping("/api/content/posts")
public class PostController {

    private final PostService postService;

    private final PostCommentRenderAssembler postCommentRenderAssembler;

    private final PostCommentService postCommentService;

    private final OptionService optionService;

    private final PostRenderAssembler postRenderAssembler;

    private final PostAuthentication postAuthentication;

    private final AbstractStringCacheStore cacheStore;

    /**
     * post controller constructor.
     *
     * @param postService post service.
     * @param postCommentRenderAssembler post comment render assembler.
     * @param postCommentService post comment service.
     * @param optionService option service.
     * @param postRenderAssembler post render assembler.
     * @param postAuthentication post authentication.
     * @param cacheStore cacheStore
     */
    public PostController(PostService postService, PostCommentRenderAssembler postCommentRenderAssembler, PostCommentService postCommentService,
                          OptionService optionService, PostRenderAssembler postRenderAssembler, PostAuthentication postAuthentication,
                          AbstractStringCacheStore cacheStore) {
        this.postService = postService;
        this.postCommentRenderAssembler = postCommentRenderAssembler;
        this.postCommentService = postCommentService;
        this.optionService = optionService;
        this.postRenderAssembler = postRenderAssembler;
        this.postAuthentication = postAuthentication;
        this.cacheStore = cacheStore;
    }

    //CS304 issue for https://github.com/halo-dev/halo/issues/1351

    /**
     * Enable users search published articles with keywords.
     *
     * @param pageable store the priority of the sort algorithm
     * @param keyword search articles with keyword
     * @param categoryId search articles with categoryId
     * @return published articles that contain keywords and specific categoryId
     */
    @GetMapping
    @ApiOperation("Lists posts")
    public Page<PostListVO> pageBy(@PageableDefault(sort = {"topPriority", "createTime"}, direction = DESC) Pageable pageable,
                                   @RequestParam(value = "keyword", required = false) String keyword,
                                   @RequestParam(value = "categoryId", required = false) Integer categoryId) {
        PostQuery postQuery = new PostQuery();
        postQuery.setKeyword(keyword);
        postQuery.setCategoryId(categoryId);
        postQuery.setStatuses(Set.of(PostStatus.PUBLISHED));
        Page<Post> postPage = postService.pageBy(postQuery, pageable);
        return postRenderAssembler.convertToListVo(postPage);
    }

    /**
     * Lists posts by keyword.
     *
     * @param keyword keyword.
     * @param pageable pageable.
     * @return post lists by keyword.
     */
    @PostMapping(value = "search")
    @ApiOperation("Lists posts by keyword")
    public Page<BasePostSimpleDTO> pageBy(@RequestParam(value = "keyword") String keyword,
                                          @PageableDefault(sort = "createTime", direction = DESC) Pageable pageable) {
        Page<Post> postPage = postService.pageBy(keyword, pageable);
        return postRenderAssembler.convertToSimple(postPage);
    }

    /**
     * get post by id.
     *
     * @param postId post id.
     * @param formatDisabled format disabled or not.
     * @param sourceDisabled source disabled or not.
     * @return post detail vo.
     */
    @GetMapping("{postId:\\d+}")
    @ApiOperation("Gets a post")
    public PostDetailVO getBy(@PathVariable("postId") Integer postId,
                              @RequestParam(value = "token", required = false, defaultValue = "") String token,
                              @RequestParam(value = "formatDisabled", required = false, defaultValue = "true") Boolean formatDisabled,
                              @RequestParam(value = "sourceDisabled", required = false, defaultValue = "false") Boolean sourceDisabled) {
        PostDetailVO postDetailVO;

        if (StringUtils.isNotBlank(token)) {
            // If the token is not empty, it means it is an admin request,
            // then verify the token.

            // verify token
            String cachedToken = cacheStore.getAny(token, String.class).orElseThrow(() -> new ForbiddenException("您没有该文章的访问权限"));
            if (!cachedToken.equals(token)) {
                throw new ForbiddenException("您没有该文章的访问权限");
            }
            Post post = postService.getWithLatestContentById(postId);
            if (PostStatus.RECYCLE.equals(post.getStatus())) {
                // Articles in the recycle bin are not allowed to be accessed.
                throw new NotFoundException("查询不到该文章的信息");
            }
            postDetailVO = postRenderAssembler.convertToPreviewDetailVo(post);
        } else {
            Post post = postService.getBy(PostStatus.PUBLISHED, postId);
            postDetailVO = postRenderAssembler.convertToDetailVo(post);
        }

        checkAuthenticate(postId);

        if (formatDisabled) {
            // Clear the format content
            postDetailVO.setContent(null);
        }

        if (sourceDisabled) {
            // Clear the original content
            postDetailVO.setOriginalContent(null);
        }

        postService.publishVisitEvent(postDetailVO.getId());

        return postDetailVO;
    }

    /**
     * get a post by slug.
     *
     * @param slug slug
     * @param formatDisabled format distabled or not
     * @param sourceDisabled source deisabled or not
     * @return post detail vo.
     */
    @GetMapping("/slug")
    @ApiOperation("Gets a post")
    public PostDetailVO getBy(@RequestParam("slug") String slug,
                              @RequestParam(value = "formatDisabled", required = false, defaultValue = "true") Boolean formatDisabled,
                              @RequestParam(value = "sourceDisabled", required = false, defaultValue = "false") Boolean sourceDisabled) {
        Post post = postService.getBy(PostStatus.PUBLISHED, slug);

        checkAuthenticate(post.getId());

        PostDetailVO postDetailVO = postRenderAssembler.convertToDetailVo(post);

        if (formatDisabled) {
            // Clear the format content
            postDetailVO.setContent(null);
        }

        if (sourceDisabled) {
            // Clear the original content
            postDetailVO.setOriginalContent(null);
        }

        postService.publishVisitEvent(postDetailVO.getId());

        return postDetailVO;
    }

    /**
     * Gets previous post by current post id.
     *
     * @param postId post id.
     * @return post detail vo.
     */
    @GetMapping("{postId:\\d+}/prev")
    @ApiOperation("Gets previous post by current post id.")
    public PostDetailVO getPrevPostBy(@PathVariable("postId") Integer postId) {
        Post post = postService.getBy(PostStatus.PUBLISHED, postId);
        Post prevPost = postService.getPrevPost(post).orElseThrow(() -> new NotFoundException("查询不到该文章的信息"));
        checkAuthenticate(prevPost.getId());
        return postRenderAssembler.convertToDetailVo(prevPost);
    }

    /**
     * Gets next post by current post id.
     *
     * @param postId post id.
     * @return post detail vo.
     */
    @GetMapping("{postId:\\d+}/next")
    @ApiOperation("Gets next post by current post id.")
    public PostDetailVO getNextPostBy(@PathVariable("postId") Integer postId) {
        Post post = postService.getBy(PostStatus.PUBLISHED, postId);
        Post nextPost = postService.getNextPost(post).orElseThrow(() -> new NotFoundException("查询不到该文章的信息"));
        checkAuthenticate(nextPost.getId());
        return postRenderAssembler.convertToDetailVo(nextPost);
    }

    /**
     * list top comments.
     *
     * @param postId post id.
     * @param page page.
     * @param sort sort.
     * @return top comments.
     */
    @GetMapping("{postId:\\d+}/comments/top_view")
    public Page<CommentWithHasChildrenVO> listTopComments(@PathVariable("postId") Integer postId,
                                                          @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                          @SortDefault(sort = "createTime", direction = DESC) Sort sort) {
        checkAuthenticate(postId);
        Page<CommentWithHasChildrenVO> comments =
            postCommentService.pageTopCommentsBy(postId, CommentStatus.PUBLISHED, PageRequest.of(page, optionService.getCommentPageSize(), sort));
        comments.getContent().forEach(postCommentRenderAssembler::clearSensitiveField);
        return comments;
    }

    /**
     * list comment children.
     *
     * @param postId post id.
     * @param commentParentId comment parent id.
     * @param sort sort.
     * @return comment list.
     */
    @GetMapping("{postId:\\d+}/comments/{commentParentId:\\d+}/children")
    public List<BaseCommentDTO> listChildrenBy(@PathVariable("postId") Integer postId, @PathVariable("commentParentId") Long commentParentId,
                                               @SortDefault(sort = "createTime", direction = DESC) Sort sort) {
        checkAuthenticate(postId);
        // Find all children comments
        List<PostComment> postComments = postCommentService.listChildrenBy(postId, commentParentId, CommentStatus.PUBLISHED, sort);
        // Convert to base comment dto

        return postCommentRenderAssembler.convertTo(postComments);
    }

    /**
     * Lists comments with tree view.
     *
     * @param postId post id.
     * @param page page
     * @param sort sort
     * @return comments with tree view.
     */
    @GetMapping("{postId:\\d+}/comments/tree_view")
    @ApiOperation("Lists comments with tree view")
    public Page<BaseCommentVO> listCommentsTree(@PathVariable("postId") Integer postId,
                                                @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                @SortDefault(sort = "createTime", direction = DESC) Sort sort) {
        checkAuthenticate(postId);
        Page<BaseCommentVO> comments = postCommentService.pageVosBy(postId, PageRequest.of(page, optionService.getCommentPageSize(), sort));
        comments.getContent().forEach(postCommentRenderAssembler::clearSensitiveField);
        return comments;
    }

    /**
     * Lists comment with list view.
     *
     * @param postId post id.
     * @param page page.
     * @param sort sort.
     * @return Lists comment with list view.
     */
    @GetMapping("{postId:\\d+}/comments/list_view")
    @ApiOperation("Lists comment with list view")
    public Page<BaseCommentWithParentVO> listComments(@PathVariable("postId") Integer postId,
                                                      @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                      @SortDefault(sort = "createTime", direction = DESC) Sort sort) {
        checkAuthenticate(postId);
        Page<BaseCommentWithParentVO> comments =
            postCommentService.pageWithParentVoBy(postId, PageRequest.of(page, optionService.getCommentPageSize(), sort));
        comments.getContent().forEach(postCommentRenderAssembler::clearSensitiveField);
        return comments;
    }

    /**
     * comments a post.
     *
     * @param postCommentParam post comment param.
     * @return post comments.
     */
    @PostMapping("comments")
    @ApiOperation("Comments a post")
    @CacheLock(autoDelete = false, traceRequest = true)
    public BaseCommentDTO comment(@RequestBody PostCommentParam postCommentParam) {
        checkAuthenticate(postCommentParam.getPostId());
        postCommentService.validateCommentBlackListStatus();

        // Escape content
        postCommentParam.setContent(HtmlUtils.htmlEscape(postCommentParam.getContent(), StandardCharsets.UTF_8.displayName()));
        return postCommentRenderAssembler.convertTo(postCommentService.createBy(postCommentParam));
    }

    /**
     * like a post.
     *
     * @param postId post id.
     */
    @PostMapping("{postId:\\d+}/likes")
    @ApiOperation("Likes a post")
    @CacheLock(autoDelete = false, traceRequest = true)
    public void like(@PathVariable("postId") @CacheParam Integer postId) {
        checkAuthenticate(postId);
        postService.increaseLike(postId);
    }

    private void checkAuthenticate(Integer postId) {
        if (!postAuthentication.isAuthenticated(postId)) {
            throw new ForbiddenException("您没有该分类的访问权限");
        }
    }
}
