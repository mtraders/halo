package run.halo.app.service.assembler.cern;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import run.halo.app.model.dto.CategoryDTO;
import run.halo.app.model.dto.TagDTO;
import run.halo.app.model.dto.cern.personnel.PersonnelDTO;
import run.halo.app.model.dto.cern.project.ProjectListDTO;
import run.halo.app.model.entity.Category;
import run.halo.app.model.entity.Tag;
import run.halo.app.model.entity.cern.Personnel;
import run.halo.app.model.entity.cern.PostPersonnel;
import run.halo.app.model.entity.cern.Project;
import run.halo.app.model.params.cern.project.ProjectQuery;
import run.halo.app.model.vo.cern.project.ProjectListVO;
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
 * project assembler.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Component
public class ProjectAssembler extends CernPostAssembler<Project> {

    private final TagService tagService;
    private final CategoryService categoryService;
    private final PostCategoryService postCategoryService;
    private final PostTagService postTagService;
    private final PersonnelService personnelService;
    private final PostPersonnelService postPersonnelService;

    /**
     * constructor of cern query service.
     *
     * @param categoryService category service.
     * @param contentService content service.
     * @param optionService option service.
     * @param tagService tag service.
     * @param postCategoryService post category service.
     * @param postTagService post tag service.
     * @param personnelService personnel service.
     * @param postPersonnelService post personnel service.
     */
    public ProjectAssembler(CategoryService categoryService, ContentService contentService, OptionService optionService, TagService tagService,
                            PostCategoryService postCategoryService, PostTagService postTagService, PersonnelService personnelService,
                            PostPersonnelService postPersonnelService) {
        super(categoryService, contentService, optionService);
        this.tagService = tagService;
        this.categoryService = categoryService;
        this.postCategoryService = postCategoryService;
        this.postTagService = postTagService;
        this.personnelService = personnelService;
        this.postPersonnelService = postPersonnelService;
    }

    /**
     * convert to project dto page.
     *
     * @param projectPage project entity page.
     * @return project list dto page
     */
    @NonNull
    public Page<ProjectListDTO> convertToListDTO(@NonNull Page<Project> projectPage) {
        Assert.notNull(projectPage, "Project page date must not be null");
        return projectPage.map(this::convertToListDTO);
    }

    /**
     * convert to list dto.
     *
     * @param project project entitoy.
     * @return project list dto.
     */
    @NonNull
    public ProjectListDTO convertToListDTO(@NonNull Project project) {
        Assert.notNull(project, "Project must not be null");
        ProjectListDTO projectListDTO = new ProjectListDTO().convertFrom(project);
        generateAndSetDTOInfoIfAbsent(project, projectListDTO);
        return projectListDTO;
    }

    /**
     * convert project entity page to project list vo page.
     *
     * @param projectPage project page.
     * @return project list vo page.
     */
    @NonNull
    public Page<ProjectListVO> convertToListVO(@NonNull Page<Project> projectPage) {
        Assert.notNull(projectPage, "Project page must not be null");
        List<ProjectListVO> projectListVOList = convertToListVO(projectPage.getContent());
        Map<Integer, ProjectListVO> projectListVOMap = ServiceUtils.convertToMap(projectListVOList, ProjectListVO::getId);
        return projectPage.map(project -> {
            Integer projectId = project.getId();
            return projectListVOMap.get(projectId);
        });
    }

    /**
     * convert project list to project list vo list.
     *
     * @param projects projects
     * @return project list vo list.
     */
    public List<ProjectListVO> convertToListVO(@NonNull List<Project> projects) {
        Set<Integer> projectIds = ServiceUtils.fetchProperty(projects, Project::getId);
        // Get tag list map
        Map<Integer, List<Tag>> tagListMap = postTagService.listTagListMapBy(projectIds);
        // Get category list map
        Map<Integer, List<Category>> categoryListMap = postCategoryService.listCategoryListMap(projectIds);
        // get author list map
        Map<Integer, List<Personnel>> managerListMap = postPersonnelService.listPersonnelListMap(projectIds);
        return projects.stream().map(project -> {
            ProjectListVO projectListVO = new ProjectListVO().convertFrom(project);
            Integer projectId = project.getId();
            List<TagDTO> tags =
                Optional.ofNullable(tagListMap.get(projectId)).orElseGet(LinkedList::new).stream().filter(Objects::nonNull).map(tagService::convertTo)
                    .collect(Collectors.toList());
            projectListVO.setTags(tags);
            List<CategoryDTO> categories =
                Optional.ofNullable(categoryListMap.get(projectId)).orElseGet(LinkedList::new).stream().filter(Objects::nonNull)
                    .map(categoryService::convertTo).collect(Collectors.toList());
            projectListVO.setCategories(categories);
            List<PersonnelDTO> managers =
                Optional.ofNullable(managerListMap.get(projectId)).orElseGet(LinkedList::new).stream().filter(Objects::nonNull)
                    .map(personnelService::convertTo).collect(Collectors.toList());
            projectListVO.setManagers(managers);
            projectListVO.setFullPath(buildFullPath(project));
            return projectListVO;
        }).collect(Collectors.toList());
    }

    /**
     * build project spec by query.
     *
     * @param projectQuery query
     * @return Specification of project.
     */
    public Specification<Project> buildSpecByQuery(ProjectQuery projectQuery) {
        Specification<Project> projectSpecification = super.buildSpecByQuery(projectQuery, Project.class);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = Lists.newLinkedList();
            Predicate predicate = projectSpecification.toPredicate(root, query, criteriaBuilder);
            predicates.add(predicate);
            // add project spec query info
            // add manager query
            Set<Integer> managerIds = projectQuery.getManagerIds();
            if (CollectionUtils.isNotEmpty(managerIds)) {
                Subquery<Project> projectSubquery = query.subquery(Project.class);
                Root<PostPersonnel> postPersonnelRoot = projectSubquery.from(PostPersonnel.class);
                projectSubquery.where(criteriaBuilder.equal(root.get("id"), postPersonnelRoot.get("postId")),
                    postPersonnelRoot.get("personnelId").in(projectQuery.getManagerIds()));
                predicates.add(criteriaBuilder.exists(projectSubquery));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
    }
}
