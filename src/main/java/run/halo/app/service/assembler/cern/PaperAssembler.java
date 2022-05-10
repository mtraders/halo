package run.halo.app.service.assembler.cern;


import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import run.halo.app.model.dto.CategoryDTO;
import run.halo.app.model.dto.TagDTO;
import run.halo.app.model.dto.cern.paper.PaperListDTO;
import run.halo.app.model.dto.cern.personnel.PersonnelDTO;
import run.halo.app.model.entity.Category;
import run.halo.app.model.entity.Content;
import run.halo.app.model.entity.Tag;
import run.halo.app.model.entity.cern.Paper;
import run.halo.app.model.entity.cern.Personnel;
import run.halo.app.model.entity.cern.PostPersonnel;
import run.halo.app.model.params.cern.paper.PaperQuery;
import run.halo.app.model.vo.cern.paper.PaperDetailVO;
import run.halo.app.model.vo.cern.paper.PaperListVO;
import run.halo.app.service.CategoryService;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.PostCategoryService;
import run.halo.app.service.PostTagService;
import run.halo.app.service.TagService;
import run.halo.app.service.cern.PersonnelService;
import run.halo.app.service.cern.PostPersonnelService;
import run.halo.app.utils.ServiceUtils;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * paper assembler.
 *
 * @author lizc
 */
@Component
public class PaperAssembler extends CernPostAssembler<Paper> {

    private final TagService tagService;
    private final CategoryService categoryService;
    private final PostCategoryService postCategoryService;
    private final PostTagService postTagService;
    private final PersonnelService personnelService;
    private final PostPersonnelService postPersonnelService;

    /**
     * post assembler.
     *
     * @param contentService content service.
     * @param tagService tag service.
     * @param categoryService category service.
     * @param optionService option service.
     * @param postTagService post tag service.
     * @param postCategoryService post category service.
     * @param personnelService personnel service.
     * @param postPersonnelService post personnel service.
     */
    public PaperAssembler(ContentService contentService, TagService tagService, CategoryService categoryService, OptionService optionService,
                          PostTagService postTagService, PostCategoryService postCategoryService, PersonnelService personnelService,
                          PostPersonnelService postPersonnelService) {
        super(categoryService, contentService, optionService);
        this.tagService = tagService;
        this.categoryService = categoryService;
        this.postTagService = postTagService;
        this.postCategoryService = postCategoryService;
        this.personnelService = personnelService;
        this.postPersonnelService = postPersonnelService;
    }

    /**
     * convert paper entity to paper list dto.
     *
     * @param paper paper entity.
     * @return paper list dto.
     */
    @NonNull
    public PaperListDTO convertToListDTO(@NonNull Paper paper) {
        Assert.notNull(paper, "paper must not be null");
        PaperListDTO paperListDTO = new PaperListDTO().convertFrom(paper);
        generateAndSetDTOInfoIfAbsent(paper, paperListDTO);
        return paperListDTO;
    }

    /**
     * convert to paper list dto page.
     *
     * @param paperPage paper page.
     * @return paper list dto page.
     */
    @NonNull
    public Page<PaperListDTO> convertToListDTO(@NonNull Page<Paper> paperPage) {
        Assert.notNull(paperPage, "paperPage must not be null");
        return paperPage.map(this::convertToListDTO);
    }

    /**
     * convert paper list to paper list.
     *
     * @param papers papers
     * @return paper vo list
     */
    public List<PaperListVO> convertToListVO(@NonNull List<Paper> papers) {
        Set<Integer> paperIds = ServiceUtils.fetchProperty(papers, Paper::getId);
        // Get tag list map
        Map<Integer, List<Tag>> tagListMap = postTagService.listTagListMapBy(paperIds);
        // Get category list map
        Map<Integer, List<Category>> categoryListMap = postCategoryService.listCategoryListMap(paperIds);
        // get author list map
        Map<Integer, List<Personnel>> authorListMap = postPersonnelService.listPersonnelListMap(paperIds);
        return papers.stream().map(paper -> {
            PaperListVO paperListVO = new PaperListVO().convertFrom(paper);
            Integer paperId = paper.getId();
            List<TagDTO> tags =
                Optional.ofNullable(tagListMap.get(paperId)).orElseGet(LinkedList::new).stream().filter(Objects::nonNull).map(tagService::convertTo)
                    .collect(Collectors.toList());
            paperListVO.setTags(tags);
            List<CategoryDTO> categories =
                Optional.ofNullable(categoryListMap.get(paperId)).orElseGet(LinkedList::new).stream().filter(Objects::nonNull)
                    .map(categoryService::convertTo).collect(Collectors.toList());
            paperListVO.setCategories(categories);
            List<PersonnelDTO> personnelDTOS =
                Optional.ofNullable(authorListMap.get(paperId)).orElseGet(LinkedList::new).stream().filter(Objects::nonNull)
                    .map(personnelService::convertTo).collect(Collectors.toList());
            paperListVO.setAuthors(personnelDTOS);
            return paperListVO;
        }).collect(Collectors.toList());
    }

    /**
     * convert paper entity page to paper list vo page.
     *
     * @param paperPage paper page
     * @return paper list vo page.
     */
    @NonNull
    public Page<PaperListVO> convertToListVO(@NonNull Page<Paper> paperPage) {
        Assert.notNull(paperPage, "Paper page must not be null");
        List<PaperListVO> paperListVOList = convertToListVO(paperPage.getContent());
        Map<Integer, PaperListVO> paperListVOMap = ServiceUtils.convertToMap(paperListVOList, PaperListVO::getId);
        return paperPage.map(paper -> {
            Integer paperId = paper.getId();
            return paperListVOMap.get(paperId);
        });
    }

    /**
     * convert to paper detail vo.
     *
     * @param paper paper entity
     * @param tags tag list
     * @param categories category list
     * @param personnelList personnel list
     * @return paper detail vo.
     */
    public PaperDetailVO convertTo(@NonNull Paper paper, @Nullable List<Tag> tags, @Nullable List<Category> categories,
                                   @Nullable List<Personnel> personnelList) {
        Assert.notNull(paper, "paper must not be null");
        PaperDetailVO detailVO = new PaperDetailVO().convertFrom(paper);
        generateAndSetDTOInfoIfAbsent(paper, detailVO);
        detailVO.setTags(tagService.convertTo(tags));
        detailVO.setCategories(categoryService.convertTo(categories));
        detailVO.setAuthors(personnelService.convertTo(personnelList));

        Content.PatchedContent newsContent = paper.getContent();
        detailVO.setContent(newsContent.getContent());
        detailVO.setOriginalContent(newsContent.getOriginalContent());
        return detailVO;
    }

    /**
     * convert to detail vo of paper entity.
     *
     * @param paper paper entity.
     * @return paper detail vo.
     */
    @NonNull
    public PaperDetailVO convertToDetailVO(@NonNull Paper paper) {
        Integer paperId = paper.getId();
        List<Tag> tags = postTagService.listTagsBy(paperId);
        List<Category> categories = postCategoryService.listCategoriesBy(paperId);
        List<Personnel> personnelList = postPersonnelService.listPersonnelListBy(paperId);
        return convertTo(paper, tags, categories, personnelList);
    }

    /**
     * build paper spec by query.
     *
     * @param paperQuery query
     * @return Specification of paper.
     */
    public Specification<Paper> buildSpecByQuery(PaperQuery paperQuery) {
        Specification<Paper> paperSpecification = super.buildSpecByQuery(paperQuery, Paper.class);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = Lists.newLinkedList();
            Predicate predicate = paperSpecification.toPredicate(root, query, criteriaBuilder);
            predicates.add(predicate);
            // add paper spec query info
            // add author query
            Set<Integer> authorIds = paperQuery.getAuthorIds();
            if (CollectionUtils.isNotEmpty(authorIds)) {
                Subquery<Paper> paperSubquery = query.subquery(Paper.class);
                Root<PostPersonnel> postPersonnelRoot = paperSubquery.from(PostPersonnel.class);
                paperSubquery.select(postPersonnelRoot.get("postId"));
                paperSubquery.where(criteriaBuilder.equal(root.get("id"), postPersonnelRoot.get("postId")),
                    postPersonnelRoot.get("personnelId").in(paperQuery.getAuthorIds()));
                predicates.add(criteriaBuilder.exists(paperSubquery));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
    }
}
