package run.halo.app.service.cern.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import run.halo.app.event.cern.ProjectUpdateEvent;
import run.halo.app.event.logger.LogEvent;
import run.halo.app.model.entity.Category;
import run.halo.app.model.entity.Content;
import run.halo.app.model.entity.PostCategory;
import run.halo.app.model.entity.PostTag;
import run.halo.app.model.entity.Tag;
import run.halo.app.model.entity.cern.Personnel;
import run.halo.app.model.entity.cern.PostPersonnel;
import run.halo.app.model.entity.cern.Project;
import run.halo.app.model.enums.LogType;
import run.halo.app.model.params.cern.project.ProjectQuery;
import run.halo.app.model.vo.cern.project.ProjectDetailVO;
import run.halo.app.repository.cern.ProjectRepository;
import run.halo.app.service.CategoryService;
import run.halo.app.service.ContentPatchLogService;
import run.halo.app.service.ContentService;
import run.halo.app.service.OptionService;
import run.halo.app.service.PostCategoryService;
import run.halo.app.service.PostTagService;
import run.halo.app.service.TagService;
import run.halo.app.service.assembler.cern.ProjectAssembler;
import run.halo.app.service.cern.PersonnelService;
import run.halo.app.service.cern.PostPersonnelService;
import run.halo.app.service.cern.ProjectService;
import run.halo.app.service.impl.BasePostServiceImpl;
import run.halo.app.utils.DateUtils;
import run.halo.app.utils.ServiceUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * project service impl.
 *
 * @author <a href="mailto:lizc@fists.cn">lizc</a>
 */
@Slf4j
@Service
public class ProjectServiceImpl extends BasePostServiceImpl<Project> implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ContentPatchLogService contentPatchLogService;
    private final ProjectAssembler projectAssembler;
    private final TagService tagService;
    private final CategoryService categoryService;
    private final PersonnelService personnelService;
    private final PostTagService postTagService;
    private final PostCategoryService postCategoryService;
    private final PostPersonnelService postPersonnelService;
    private final ContentService contentService;
    private final ApplicationContext applicationContext;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructor of project service.
     *
     * @param projectRepository project repo.
     * @param optionService option service.
     * @param contentService content service.
     * @param contentPatchLogService content patch log service.
     * @param projectAssembler project assembler.
     * @param tagService tag service.
     * @param categoryService category service.
     * @param personnelService personnel service.
     * @param postTagService post tag service.
     * @param postCategoryService post category service.
     * @param postPersonnelService post personnel service.
     * @param applicationContext application context.
     * @param eventPublisher event publisher
     */
    public ProjectServiceImpl(ProjectRepository projectRepository, OptionService optionService, ContentService contentService,
                              ContentPatchLogService contentPatchLogService, ProjectAssembler projectAssembler,
                              TagService tagService, CategoryService categoryService, PersonnelService personnelService,
                              PostTagService postTagService,
                              PostCategoryService postCategoryService,
                              PostPersonnelService postPersonnelService, ApplicationContext applicationContext,
                              ApplicationEventPublisher eventPublisher) {
        super(projectRepository, optionService, contentService, contentPatchLogService);
        this.projectRepository = projectRepository;
        this.contentPatchLogService = contentPatchLogService;
        this.projectAssembler = projectAssembler;
        this.tagService = tagService;
        this.categoryService = categoryService;
        this.personnelService = personnelService;
        this.postTagService = postTagService;
        this.postCategoryService = postCategoryService;
        this.postPersonnelService = postPersonnelService;
        this.contentService = contentService;
        this.applicationContext = applicationContext;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Get post with the latest content by id.
     * content from patch log.
     *
     * @param postId post id.
     * @return post with the latest content.
     */
    @Override
    public Project getWithLatestContentById(Integer postId) {
        Project project = getById(postId);
        Content content = getContentById(postId);
        Content.PatchedContent patchedContent = contentPatchLogService.getPatchedContentById(content.getHeadPatchLogId());
        project.setContent(patchedContent);
        return project;
    }

    /**
     * update project by project param.
     *
     * @param project project entity.
     * @param tagIds tag ids
     * @param categoryIds category ids
     * @param managerIds manager ids.
     * @param autoSave auto-save flag.
     * @return project detail vo.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @NonNull
    public ProjectDetailVO updateBy(@NonNull Project project, Set<Integer> tagIds, Set<Integer> categoryIds, Set<Integer> managerIds,
                                    boolean autoSave) {
        project.setEditTime(DateUtils.now());
        ProjectDetailVO detailVO = createOrUpdate(project, tagIds, categoryIds, managerIds);
        if (!autoSave) {
            LogEvent logEvent = new LogEvent(this, detailVO.getId().toString(), LogType.PROJECT_EDITED, detailVO.getTitle());
            eventPublisher.publishEvent(logEvent);
        }
        return detailVO;
    }

    /**
     * remove project by ids.
     *
     * @param ids project ids
     * @return deleted projects.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @NonNull
    public List<Project> removeByIds(Collection<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return ids.stream().map(this::removeById).collect(Collectors.toList());
    }


    /**
     * Removes by id.
     *
     * @param projectId project id
     * @return DOMAIN
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @NonNull
    public Project removeById(@NonNull Integer projectId) {
        Assert.notNull(projectId, "Project id must not be null");
        log.debug("Removing project: {}", projectId);
        // Remove project tags
        List<PostTag> projectTags = postTagService.removeByPostId(projectId);
        log.debug("Removed project tags: [{}]", projectTags);
        // Remove project categories
        List<PostCategory> projectCategories = postCategoryService.removeByPostId(projectId);
        log.debug("Remove project categories: [{}]", projectCategories);
        // Remove project personnel
        List<PostPersonnel> projectManagers = postPersonnelService.removeByPostId(projectId);
        log.debug("Remove project managers: [{}]", projectManagers);
        // Remove project content
        Content projectContent = contentService.removeById(projectId);
        Project deletedProject = super.removeById(projectId);
        eventPublisher.publishEvent(new LogEvent(this, projectId.toString(), LogType.PROJECT_DELETED, deletedProject.getTitle()));
        return deletedProject;
    }

    /**
     * create project by project param.
     *
     * @param project project entity.
     * @param tagIds tag id list.
     * @param categoryIds category id list.
     * @param managerIds manager id list.
     * @param autoSave auto-save flag.
     * @return project detail vo.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @NonNull
    public ProjectDetailVO createBy(@NonNull Project project, Set<Integer> tagIds, Set<Integer> categoryIds, Set<Integer> managerIds,
                                    boolean autoSave) {
        ProjectDetailVO detailVO = createOrUpdate(project, tagIds, categoryIds, managerIds);
        if (!autoSave) {
            LogEvent logEvent = new LogEvent(this, detailVO.getId().toString(), LogType.PROJECT_PUBLISHED, detailVO.getTitle());
            eventPublisher.publishEvent(logEvent);
        }
        return detailVO;
    }

    private ProjectDetailVO createOrUpdate(@NonNull Project project, Set<Integer> tagIds, Set<Integer> categoryIds, Set<Integer> managerIds) {
        Assert.notNull(project, "Project must not be null");
        project = super.createOrUpdateBy(project);
        Integer projectId = project.getId();
        postTagService.removeByPostId(projectId);
        postCategoryService.removeByPostId(projectId);
        postPersonnelService.removeByPostId(projectId);

        // List all tags
        List<Tag> tags = tagService.listAllByIds(tagIds);
        // List all categories
        List<Category> categories = categoryService.listAllByIds(categoryIds);
        // List all personnel
        List<Personnel> managers = personnelService.listAllByIds(managerIds);

        List<PostTag> projectTags = postTagService.mergeOrCreateByIfAbsent(projectId, ServiceUtils.fetchProperty(tags, Tag::getId));
        log.debug("Created project tags: [{}]", projectTags);
        List<PostCategory> projectCategories =
            postCategoryService.mergeOrCreateByIfAbsent(projectId, ServiceUtils.fetchProperty(categories, Category::getId));
        log.debug("Created project categories: [{}]", projectCategories);
        List<PostPersonnel> projectManagers =
            postPersonnelService.mergeOrCreateByIfAbsent(projectId, ServiceUtils.fetchProperty(managers, Personnel::getId));
        log.debug("Created project managers: [{}]", projectManagers);

        applicationContext.publishEvent(new ProjectUpdateEvent(this, project));

        // get draft content by head patch log id
        Content postContent = contentService.getById(projectId);
        project.setContent(contentPatchLogService.getPatchedContentById(postContent.getHeadPatchLogId()));
        return projectAssembler.convertTo(project, tags, categories, managers);
    }

    /**
     * Page project.
     *
     * @param projectQuery project query.
     * @param pageable page info.
     * @return project page.
     */
    @Override
    @NonNull
    public Page<Project> pageBy(@NonNull ProjectQuery projectQuery, @NonNull Pageable pageable) {
        Assert.notNull(projectQuery, "Project query must not be null");
        Assert.notNull(pageable, "Project page info must not be null");
        Specification<Project> projectSpecification = projectAssembler.buildSpecByQuery(projectQuery);
        return projectRepository.findAll(projectSpecification, pageable);
    }

}
