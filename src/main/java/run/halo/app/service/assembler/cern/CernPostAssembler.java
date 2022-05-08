package run.halo.app.service.assembler.cern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import run.halo.app.model.dto.cern.CernPostListDTO;
import run.halo.app.model.entity.BasePost;
import run.halo.app.model.entity.Category;
import run.halo.app.model.entity.Content;
import run.halo.app.model.entity.PostCategory;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.params.cern.CernPostQuery;
import run.halo.app.service.CategoryService;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.assembler.BasePostAssembler;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Cern assembler.
 *
 * @param <POST> post extends base post.
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
public class CernPostAssembler<POST extends BasePost> extends BasePostAssembler<POST> {
    private final CategoryService categoryService;
    private final ContentService contentService;

    /**
     * constructor of cern query service.
     *
     * @param categoryService category service.
     * @param contentService content service.
     * @param optionService option service.
     */
    public CernPostAssembler(CategoryService categoryService, ContentService contentService, OptionService optionService) {
        super(contentService, optionService);
        this.categoryService = categoryService;
        this.contentService = contentService;
    }

    /**
     * build cern spec by query.
     *
     * @param cernPostQuery cern post query
     * @param postClazz post class.
     * @return Specification
     */
    public Specification<POST> buildSpecByQuery(CernPostQuery<POST> cernPostQuery, Class<POST> postClazz) {
        Assert.notNull(cernPostQuery, "cern query must not be null");
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();

            Set<PostStatus> statuses = cernPostQuery.getStatuses();
            if (!CollectionUtils.isEmpty(statuses)) {
                predicates.add(root.get("status").in(statuses));
            }

            if (cernPostQuery.getCategoryId() != null) {
                List<Integer> categoryIds =
                    categoryService.listAllByParentId(cernPostQuery.getCategoryId()).stream().map(Category::getId).collect(Collectors.toList());
                Subquery<POST> postSubQuery = query.subquery(postClazz);
                Root<PostCategory> postCategoryRoot = postSubQuery.from(PostCategory.class);
                postSubQuery.select(postCategoryRoot.get("postId"));
                postSubQuery.where(criteriaBuilder.equal(root.get("id"), postCategoryRoot.get("postId")),
                    postCategoryRoot.get("categoryId").in(categoryIds));
                predicates.add(criteriaBuilder.exists(postSubQuery));
            }

            if (cernPostQuery.getKeyword() != null) {

                // Format like condition
                String likeCondition = String.format("%%%s%%", StringUtils.strip(cernPostQuery.getKeyword()));

                // Build like predicate
                Subquery<POST> postSubQuery = query.subquery(postClazz);
                Root<Content> contentRoot = postSubQuery.from(Content.class);
                postSubQuery.select(contentRoot.get("id")).where(criteriaBuilder.like(contentRoot.get("originalContent"), likeCondition));

                Predicate titleLike = criteriaBuilder.like(root.get("title"), likeCondition);

                predicates.add(criteriaBuilder.or(titleLike, criteriaBuilder.in(root).value(postSubQuery)));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
    }

    /**
     * generate and set summary if absent to dto.
     *
     * @param post post entity.
     * @param postDTOT post dto.
     * @param <DTOT> post dto.
     */
    private <DTOT extends CernPostListDTO<POST>> void generateAndSetSummaryIfAbsent(@NonNull POST post, @NonNull DTOT postDTOT) {
        Assert.notNull(post, "The post must not be null.");
        if (StringUtils.isNotBlank(postDTOT.getSummary())) {
            return;
        }
        Content.PatchedContent patchedContent = post.getContentOfNullable();
        if (patchedContent == null) {
            Content newsContent = contentService.getByIdOfNullable(post.getId());
            if (newsContent != null) {
                postDTOT.setSummary(generateSummary(newsContent.getContent()));
            } else {
                postDTOT.setSummary(StringUtils.EMPTY);
            }
        } else {
            postDTOT.setSummary(generateSummary(patchedContent.getContent()));
        }
    }

    /**
     * generate dto info if absent.
     *
     * @param post post entity
     * @param postDTOT post dto
     * @param <DTOT> post dto.
     */
    public <DTOT extends CernPostListDTO<POST>> void generateAndSetDTOInfoIfAbsent(@NonNull POST post, @NonNull DTOT postDTOT) {
        // summary
        generateAndSetSummaryIfAbsent(post, postDTOT);
        // Post currently drafting in process
        Boolean isInProcess = contentService.draftingInProgress(post.getId());
        postDTOT.setInProgress(isInProcess);
        // full path
        postDTOT.setFullPath(buildFullPath(post));
    }
}
