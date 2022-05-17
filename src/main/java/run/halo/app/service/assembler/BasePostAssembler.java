package run.halo.app.service.assembler;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import run.halo.app.model.dto.post.BasePostDetailDTO;
import run.halo.app.model.dto.post.BasePostMinimalDTO;
import run.halo.app.model.dto.post.BasePostSimpleDTO;
import run.halo.app.model.entity.BasePost;
import run.halo.app.model.entity.Content;
import run.halo.app.model.entity.Content.PatchedContent;
import run.halo.app.model.enums.PostPermalinkType;
import run.halo.app.model.properties.PostProperties;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.utils.DateUtils;
import run.halo.app.utils.HaloUtils;

import static run.halo.app.model.support.HaloConst.URL_SEPARATOR;

/**
 * base post assembler.
 *
 * @author guqing
 * @date 2022-03-01
 */
public class BasePostAssembler<POST extends BasePost> {
    private static final Pattern summaryPattern = Pattern.compile("\t|\r|\n");

    private final ContentService contentService;

    private final OptionService optionService;

    public BasePostAssembler(ContentService contentService, OptionService optionService) {
        this.contentService = contentService;
        this.optionService = optionService;
    }

    /**
     * Convert POST to minimal dto.
     *
     * @param post post must not be null.
     * @return minimal dto.
     */
    public BasePostMinimalDTO convertToMinimal(POST post) {
        Assert.notNull(post, "Post must not be null");

        return new BasePostMinimalDTO().convertFrom(post);
    }

    /**
     * Convert list of POST to minimal dto of list.
     *
     * @param posts posts must not be null.
     * @return a list of minimal dto.
     */
    @NonNull
    public List<BasePostMinimalDTO> convertToMinimal(List<POST> posts) {
        if (CollectionUtils.isEmpty(posts)) {
            return Collections.emptyList();
        }

        return posts.stream().map(this::convertToMinimal).collect(Collectors.toList());
    }

    /**
     * Convert page of POST to minimal dto of page.
     *
     * @param postPage postPage must not be null.
     * @return a page of minimal dto.
     */
    @NonNull
    public Page<BasePostMinimalDTO> convertToMinimal(Page<POST> postPage) {
        Assert.notNull(postPage, "Post page must not be null");

        return postPage.map(this::convertToMinimal);
    }

    /**
     * Convert POST to simple dto.
     *
     * @param post post must not be null.
     * @return simple dto.
     */
    @NonNull
    public BasePostSimpleDTO convertToSimple(POST post) {
        Assert.notNull(post, "Post must not be null");

        BasePostSimpleDTO basePostSimpleDTO = new BasePostSimpleDTO().convertFrom(post);

        // Set summary
        generateAndSetSummaryIfAbsent(post, basePostSimpleDTO);

        // Post currently drafting in process
        Boolean isInProcess = contentService.draftingInProgress(post.getId());
        basePostSimpleDTO.setInProgress(isInProcess);

        return basePostSimpleDTO;
    }

    /**
     * Convert list of POST to list of simple dto.
     *
     * @param posts posts must not be null.
     * @return a list of simple dto.
     */
    @NonNull
    public List<BasePostSimpleDTO> convertToSimple(List<POST> posts) {
        if (CollectionUtils.isEmpty(posts)) {
            return Collections.emptyList();
        }

        return posts.stream().map(this::convertToSimple).collect(Collectors.toList());
    }

    /**
     * Convert page of POST to page of simple dto.
     *
     * @param postPage postPage must not be null.
     * @return a page of simple dto.
     */
    @NonNull
    public Page<BasePostSimpleDTO> convertToSimple(Page<POST> postPage) {
        Assert.notNull(postPage, "Post page must not be null");

        return postPage.map(this::convertToSimple);
    }

    /**
     * Convert POST to detail dto.
     *
     * @param post post must not be null.
     * @return detail dto.
     */
    @NonNull
    public BasePostDetailDTO convertToDetail(POST post) {
        Assert.notNull(post, "Post must not be null");

        BasePostDetailDTO postDetail = new BasePostDetailDTO().convertFrom(post);

        // Post currently drafting in process
        Boolean isInProcess = contentService.draftingInProgress(post.getId());
        postDetail.setInProgress(isInProcess);

        return postDetail;
    }

    /**
     * generate summary by html content.
     *
     * @param htmlContent html content.
     * @return summary
     */
    @NonNull
    protected String generateSummary(@Nullable String htmlContent) {
        if (StringUtils.isBlank(htmlContent)) {
            return StringUtils.EMPTY;
        }

        String text = HaloUtils.cleanHtmlTag(htmlContent);

        Matcher matcher = summaryPattern.matcher(text);
        text = matcher.replaceAll("");

        // Get summary length
        Integer summaryLength = optionService.getByPropertyOrDefault(PostProperties.SUMMARY_LENGTH, Integer.class, 150);

        return StringUtils.substring(text, 0, summaryLength);
    }

    protected <T extends BasePostSimpleDTO> void generateAndSetSummaryIfAbsent(POST post, T postVo) {
        Assert.notNull(post, "The post must not be null.");
        if (StringUtils.isNotBlank(postVo.getSummary())) {
            return;
        }

        PatchedContent patchedContent = post.getContentOfNullable();
        if (patchedContent == null) {
            Content postContent = contentService.getByIdOfNullable(post.getId());
            if (postContent != null) {
                postVo.setSummary(generateSummary(postContent.getContent()));
            } else {
                postVo.setSummary(StringUtils.EMPTY);
            }
        } else {
            postVo.setSummary(generateSummary(patchedContent.getContent()));
        }
    }

    /**
     * Build post full path.
     *
     * @param post post
     * @return full patch to access.
     */
    public String buildFullPath(POST post) {

        PostPermalinkType permalinkType = optionService.getPostPermalinkType();

        String pathSuffix = optionService.getPathSuffix();

        String archivesPrefix = optionService.getArchivesPrefix();

        int month = DateUtils.month(post.getCreateTime()) + 1;

        String monthString = month < 10 ? "0" + month : String.valueOf(month);

        int day = DateUtils.dayOfMonth(post.getCreateTime());

        String dayString = day < 10 ? "0" + day : String.valueOf(day);

        StringBuilder fullPath = new StringBuilder();

        if (optionService.isEnabledAbsolutePath()) {
            fullPath.append(optionService.getBlogBaseUrl());
        }

        fullPath.append(URL_SEPARATOR);

        if (permalinkType.equals(PostPermalinkType.DEFAULT)) {
            fullPath.append(archivesPrefix).append(URL_SEPARATOR).append(post.getSlug()).append(pathSuffix);
        } else if (permalinkType.equals(PostPermalinkType.ID)) {
            fullPath.append("?p=").append(post.getId());
        } else if (permalinkType.equals(PostPermalinkType.DATE)) {
            fullPath.append(DateUtils.year(post.getCreateTime())).append(URL_SEPARATOR).append(monthString).append(URL_SEPARATOR)
                .append(post.getSlug()).append(pathSuffix);
        } else if (permalinkType.equals(PostPermalinkType.DAY)) {
            fullPath.append(DateUtils.year(post.getCreateTime())).append(URL_SEPARATOR).append(monthString).append(URL_SEPARATOR).append(dayString)
                .append(URL_SEPARATOR).append(post.getSlug()).append(pathSuffix);
        } else if (permalinkType.equals(PostPermalinkType.YEAR)) {
            fullPath.append(DateUtils.year(post.getCreateTime())).append(URL_SEPARATOR).append(post.getSlug()).append(pathSuffix);
        } else if (permalinkType.equals(PostPermalinkType.ID_SLUG)) {
            fullPath.append(archivesPrefix).append(URL_SEPARATOR).append(post.getId()).append(pathSuffix);
        } else if (permalinkType.equals(PostPermalinkType.CERN_ID)) {
            fullPath.append("post").append(URL_SEPARATOR).append(post.getId());
        }
        return fullPath.toString();
    }
}
