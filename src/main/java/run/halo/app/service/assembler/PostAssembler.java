package run.halo.app.service.assembler;

import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import run.halo.app.model.dto.post.BasePostMinimalDTO;
import run.halo.app.model.entity.Category;
import run.halo.app.model.entity.Content.PatchedContent;
import run.halo.app.model.entity.Post;
import run.halo.app.model.entity.PostMeta;
import run.halo.app.model.entity.Tag;
import run.halo.app.model.entity.User;
import run.halo.app.model.enums.CommentStatus;
import run.halo.app.model.vo.ArchiveMonthVO;
import run.halo.app.model.vo.ArchiveYearVO;
import run.halo.app.model.vo.PostDetailVO;
import run.halo.app.model.vo.PostListVO;
import run.halo.app.service.CategoryService;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.PostCategoryService;
import run.halo.app.service.PostCommentService;
import run.halo.app.service.PostMetaService;
import run.halo.app.service.PostTagService;
import run.halo.app.service.TagService;
import run.halo.app.service.UserService;
import run.halo.app.service.cern.PostUserService;
import run.halo.app.utils.DateUtils;
import run.halo.app.utils.ServiceUtils;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Post assembler.
 *
 * @author guqing
 * @date 2022-03-01
 */
@Component
public class PostAssembler extends BasePostAssembler<Post> {

    private final PostTagService postTagService;

    private final PostCategoryService postCategoryService;

    private final PostMetaService postMetaService;

    private final PostCommentService postCommentService;

    private final TagService tagService;

    private final CategoryService categoryService;

    private final ContentService contentService;

    private final OptionService optionService;

    private final UserService userService;
    private final PostUserService postUserService;

    /**
     * post assembler constructor.
     *
     * @param contentService content service.
     * @param optionService option service.
     * @param postTagService post tag service.
     * @param postCategoryService post category service.
     * @param postMetaService post meta service.
     * @param postCommentService post comment service.
     * @param tagService tag service.
     * @param categoryService category service.
     * @param userService user service.
     * @param postUserService post user service.
     */
    public PostAssembler(ContentService contentService, OptionService optionService, PostTagService postTagService,
                         PostCategoryService postCategoryService, PostMetaService postMetaService, PostCommentService postCommentService,
                         TagService tagService, CategoryService categoryService, UserService userService, PostUserService postUserService) {
        super(contentService, optionService);
        this.postTagService = postTagService;
        this.postCategoryService = postCategoryService;
        this.postMetaService = postMetaService;
        this.postCommentService = postCommentService;
        this.tagService = tagService;
        this.categoryService = categoryService;
        this.contentService = contentService;
        this.optionService = optionService;
        this.userService = userService;
        this.postUserService = postUserService;
    }

    /**
     * convert to minimal.
     *
     * @param post post must not be null.
     * @return base post minimal dto.
     */
    @Override
    public BasePostMinimalDTO convertToMinimal(Post post) {
        Assert.notNull(post, "Post must not be null");
        BasePostMinimalDTO basePostMinimalDTO = new BasePostMinimalDTO().convertFrom(post);

        basePostMinimalDTO.setFullPath(buildFullPath(post));

        return basePostMinimalDTO;
    }

    /**
     * convert to minimal.
     *
     * @param posts posts must not be null.
     * @return base post minimal dto list.
     */
    @Override
    @NonNull
    public List<BasePostMinimalDTO> convertToMinimal(List<Post> posts) {
        if (CollectionUtils.isEmpty(posts)) {
            return Collections.emptyList();
        }

        return posts.stream().map(this::convertToMinimal).collect(Collectors.toList());
    }

    /**
     * Converts to detail vo.
     *
     * @param post post must not be null
     * @return post detail vo
     */
    @NonNull
    public PostDetailVO convertToDetailVo(Post post) {
        // List tags
        List<Tag> tags = postTagService.listTagsBy(post.getId());
        // List categories
        List<Category> categories = postCategoryService.listCategoriesBy(post.getId());
        // List metas
        List<PostMeta> metas = postMetaService.listBy(post.getId());
        // List users
        List<User> users = postUserService.listUsersBy(post.getId());
        // Convert to detail vo
        return convertTo(post, tags, categories, metas, users);
    }

    /**
     * Converts to a page of detail vo.
     *
     * @param postPage post page must not be null
     * @return a page of post detail vo
     */
    public Page<PostDetailVO> convertToDetailVo(Page<Post> postPage) {
        Assert.notNull(postPage, "Post page must not be null");
        return postPage.map(this::convertToDetailVo);
    }

    /**
     * Converts to a page of post list vo.
     *
     * @param postPage post page must not be null
     * @return a page of post list vo
     */
    @NonNull
    public Page<PostListVO> convertToListVo(Page<Post> postPage) {
        Assert.notNull(postPage, "Post page must not be null");

        List<Post> posts = postPage.getContent();

        Set<Integer> postIds = ServiceUtils.fetchProperty(posts, Post::getId);

        // Get tag list map
        Map<Integer, List<Tag>> tagListMap = postTagService.listTagListMapBy(postIds);

        // Get category list map
        Map<Integer, List<Category>> categoryListMap = postCategoryService.listCategoryListMap(postIds);

        // Get user list map
        Map<Integer, List<User>> userListMap = postUserService.listUserListMap(postIds);

        // Get comment count
        Map<Integer, Long> commentCountMap = postCommentService.countByStatusAndPostIds(CommentStatus.PUBLISHED, postIds);

        // Get post meta list map
        Map<Integer, List<PostMeta>> postMetaListMap = postMetaService.listPostMetaAsMap(postIds);

        return postPage.map(post -> {
            PostListVO postListVO = new PostListVO().convertFrom(post);

            generateAndSetSummaryIfAbsent(post, postListVO);

            Optional.ofNullable(tagListMap.get(post.getId())).orElseGet(LinkedList::new);

            // Set tags
            postListVO.setTags(Optional.ofNullable(tagListMap.get(post.getId())).orElseGet(LinkedList::new).stream().filter(Objects::nonNull)
                .map(tagService::convertTo).collect(Collectors.toList()));

            // Set categories
            postListVO.setCategories(
                Optional.ofNullable(categoryListMap.get(post.getId())).orElseGet(LinkedList::new).stream().filter(Objects::nonNull)
                    .map(categoryService::convertTo).collect(Collectors.toList()));

            // Set post metas
            List<PostMeta> metas = Optional.ofNullable(postMetaListMap.get(post.getId())).orElseGet(LinkedList::new);
            postListVO.setMetas(postMetaService.convertToMap(metas));

            // set users
            postListVO.setUsers(Optional.ofNullable(userListMap.get(post.getId())).orElseGet(LinkedList::new).stream().filter(Objects::nonNull)
                .map(userService::convertTo).collect(Collectors.toList()));

            // Set comment count
            postListVO.setCommentCount(commentCountMap.getOrDefault(post.getId(), 0L));

            postListVO.setFullPath(buildFullPath(post));

            // Post currently drafting in process
            Boolean isInProcess = contentService.draftingInProgress(post.getId());
            postListVO.setInProgress(isInProcess);

            return postListVO;
        });
    }

    /**
     * convert post entities to post list vos.
     *
     * @param posts posts.
     * @return post list vos.
     */
    public List<PostListVO> convertToListVo(List<Post> posts) {
        Assert.notNull(posts, "Post page must not be null");

        Set<Integer> postIds = ServiceUtils.fetchProperty(posts, Post::getId);

        // Get tag list map
        Map<Integer, List<Tag>> tagListMap = postTagService.listTagListMapBy(postIds);

        // Get category list map
        Map<Integer, List<Category>> categoryListMap = postCategoryService.listCategoryListMap(postIds);

        // Get user list map
        Map<Integer, List<User>> userListMap = postUserService.listUserListMap(postIds);

        // Get comment count
        Map<Integer, Long> commentCountMap = postCommentService.countByStatusAndPostIds(CommentStatus.PUBLISHED, postIds);

        // Get post meta list map
        Map<Integer, List<PostMeta>> postMetaListMap = postMetaService.listPostMetaAsMap(postIds);

        return posts.stream().map(post -> {
            PostListVO postListVO = new PostListVO().convertFrom(post);

            generateAndSetSummaryIfAbsent(post, postListVO);

            Optional.ofNullable(tagListMap.get(post.getId())).orElseGet(LinkedList::new);

            // Set tags
            postListVO.setTags(Optional.ofNullable(tagListMap.get(post.getId())).orElseGet(LinkedList::new).stream().filter(Objects::nonNull)
                .map(tagService::convertTo).collect(Collectors.toList()));

            // Set categories
            postListVO.setCategories(
                Optional.ofNullable(categoryListMap.get(post.getId())).orElseGet(LinkedList::new).stream().filter(Objects::nonNull)
                    .map(categoryService::convertTo).collect(Collectors.toList()));

            // Set post metas
            List<PostMeta> metas = Optional.ofNullable(postMetaListMap.get(post.getId())).orElseGet(LinkedList::new);
            postListVO.setMetas(postMetaService.convertToMap(metas));

            // set users
            postListVO.setUsers(Optional.ofNullable(userListMap.get(post.getId())).orElseGet(LinkedList::new).stream().filter(Objects::nonNull)
                .map(userService::convertTo).collect(Collectors.toList()));

            // Set comment count
            postListVO.setCommentCount(commentCountMap.getOrDefault(post.getId(), 0L));

            postListVO.setFullPath(buildFullPath(post));

            return postListVO;
        }).collect(Collectors.toList());
    }


    /**
     * Converts to post detail vo.
     *
     * @param post post must not be null
     * @param tags tags
     * @param categories categories
     * @param postMetaList postMetaList
     * @return post detail vo
     */
    @NonNull
    public PostDetailVO convertTo(@NonNull Post post, @Nullable List<Tag> tags, @Nullable List<Category> categories, List<PostMeta> postMetaList,
                                  List<User> users) {
        Assert.notNull(post, "Post must not be null");

        // Convert to base detail vo
        PostDetailVO postDetailVO = new PostDetailVO().convertFrom(post);
        generateAndSetSummaryIfAbsent(post, postDetailVO);

        // Extract ids
        Set<Integer> tagIds = ServiceUtils.fetchProperty(tags, Tag::getId);
        Set<Integer> categoryIds = ServiceUtils.fetchProperty(categories, Category::getId);
        Set<Long> metaIds = ServiceUtils.fetchProperty(postMetaList, PostMeta::getId);

        // Get post tag ids
        postDetailVO.setTagIds(tagIds);
        postDetailVO.setTags(tagService.convertTo(tags));

        // Get post category ids
        postDetailVO.setCategoryIds(categoryIds);
        postDetailVO.setCategories(categoryService.convertTo(categories));

        // Get post meta ids
        postDetailVO.setMetaIds(metaIds);
        postDetailVO.setMetas(postMetaService.convertTo(postMetaList));

        // get post user ids
        Set<Integer> userIds = ServiceUtils.fetchProperty(users, User::getId);
        postDetailVO.setUserIds(userIds);
        postDetailVO.setUsers(userService.convertTo(users));

        postDetailVO.setCommentCount(postCommentService.countByStatusAndPostId(CommentStatus.PUBLISHED, post.getId()));

        postDetailVO.setFullPath(buildFullPath(post));

        PatchedContent postContent = post.getContent();
        postDetailVO.setContent(postContent.getContent());
        postDetailVO.setOriginalContent(postContent.getOriginalContent());

        // Post currently drafting in process
        Boolean inProgress = contentService.draftingInProgress(post.getId());
        postDetailVO.setInProgress(inProgress);

        return postDetailVO;
    }


    /**
     * Convert to year archives.
     *
     * @param posts posts must not be null
     * @return list of ArchiveYearVO
     */
    public List<ArchiveYearVO> convertToYearArchives(List<Post> posts) {
        Map<Integer, List<Post>> yearPostMap = new HashMap<>(8);

        posts.forEach(post -> {
            Calendar calendar = DateUtils.convertTo(post.getCreateTime());
            yearPostMap.computeIfAbsent(calendar.get(Calendar.YEAR), year -> new LinkedList<>()).add(post);
        });

        List<ArchiveYearVO> archives = new LinkedList<>();

        yearPostMap.forEach((year, postList) -> {
            // Build archive
            ArchiveYearVO archive = new ArchiveYearVO();
            archive.setYear(year);
            archive.setPosts(convertToListVo(postList));

            // Add archive
            archives.add(archive);
        });

        // Sort this list
        archives.sort(new ArchiveYearVO.ArchiveComparator());

        return archives;
    }

    /**
     * Convert to month archives.
     *
     * @param posts posts must not be null
     * @return list of ArchiveMonthVO
     */
    public List<ArchiveMonthVO> convertToMonthArchives(List<Post> posts) {

        Map<Integer, Map<Integer, List<Post>>> yearMonthPostMap = new HashMap<>(8);

        posts.forEach(post -> {
            Calendar calendar = DateUtils.convertTo(post.getCreateTime());

            yearMonthPostMap.computeIfAbsent(calendar.get(Calendar.YEAR), year -> new HashMap<>())
                .computeIfAbsent(calendar.get(Calendar.MONTH) + 1, month -> new LinkedList<>()).add(post);
        });

        List<ArchiveMonthVO> archives = new LinkedList<>();

        yearMonthPostMap.forEach((year, monthPostMap) -> monthPostMap.forEach((month, postList) -> {
            ArchiveMonthVO archive = new ArchiveMonthVO();
            archive.setYear(year);
            archive.setMonth(month);
            archive.setPosts(convertToListVo(postList));

            archives.add(archive);
        }));

        // Sort this list
        archives.sort(new ArchiveMonthVO.ArchiveComparator());

        return archives;
    }
}
