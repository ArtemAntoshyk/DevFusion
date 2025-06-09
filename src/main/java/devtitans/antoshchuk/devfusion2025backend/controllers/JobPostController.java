package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.request.JobPostFilterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostDetailedResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.request.JobPostCreateRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobTypeDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobGradationDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.RequiredExperienceDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.TagDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.JobPostSkillDTO;
import devtitans.antoshchuk.devfusion2025backend.models.user.*;
import devtitans.antoshchuk.devfusion2025backend.services.JobPostService;
import devtitans.antoshchuk.devfusion2025backend.services.JobViewHistoryService;
import devtitans.antoshchuk.devfusion2025backend.services.JobNotificationService;
import devtitans.antoshchuk.devfusion2025backend.security.detail.CustomUserDetails;
import devtitans.antoshchuk.devfusion2025backend.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import devtitans.antoshchuk.devfusion2025backend.repositories.TagRepository;
import devtitans.antoshchuk.devfusion2025backend.repositories.SkillRepository;
import devtitans.antoshchuk.devfusion2025backend.repositories.JobGradationRepository;
import devtitans.antoshchuk.devfusion2025backend.repositories.JobTypeRepository;
import devtitans.antoshchuk.devfusion2025backend.repositories.RequiredExperienceRepository;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import devtitans.antoshchuk.devfusion2025backend.models.job.Tag;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobType;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobGradation;
import devtitans.antoshchuk.devfusion2025backend.models.job.RequiredExperience;
import devtitans.antoshchuk.devfusion2025backend.repositories.JobPostSkillRepository;
import devtitans.antoshchuk.devfusion2025backend.repositories.JobPostActivityRepository;
import devtitans.antoshchuk.devfusion2025backend.repositories.SeekerRepository;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPostActivity;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyJobPostShortDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostApplicantDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.request.JobPostActivityStatusUpdateRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.StandardResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/job-posts")
public class JobPostController {

    private static final Logger log = LoggerFactory.getLogger(JobPostController.class);

    private final JobPostService jobPostService;
    private final JobViewHistoryService jobViewHistoryService;
    private final JobNotificationService jobNotificationService;
    private final TagRepository tagRepository;
    private final SkillRepository skillRepository;
    private final JobGradationRepository jobGradationRepository;
    private final JobTypeRepository jobTypeRepository;
    private final RequiredExperienceRepository requiredExperienceRepository;
    private final JobPostSkillRepository jobPostSkillRepository;
    private final JobPostActivityRepository jobPostActivityRepository;
    private final SeekerRepository seekerRepository;

    @Autowired
    public JobPostController(JobPostService jobPostService, JobViewHistoryService jobViewHistoryService, JobNotificationService jobNotificationService,
                            TagRepository tagRepository, SkillRepository skillRepository, JobGradationRepository jobGradationRepository,
                            JobTypeRepository jobTypeRepository, RequiredExperienceRepository requiredExperienceRepository,
                            JobPostSkillRepository jobPostSkillRepository, JobPostActivityRepository jobPostActivityRepository,
                            SeekerRepository seekerRepository) {
        this.jobPostService = jobPostService;
        this.jobViewHistoryService = jobViewHistoryService;
        this.jobNotificationService = jobNotificationService;
        this.tagRepository = tagRepository;
        this.skillRepository = skillRepository;
        this.jobGradationRepository = jobGradationRepository;
        this.jobTypeRepository = jobTypeRepository;
        this.requiredExperienceRepository = requiredExperienceRepository;
        this.jobPostSkillRepository = jobPostSkillRepository;
        this.jobPostActivityRepository = jobPostActivityRepository;
        this.seekerRepository = seekerRepository;
    }

    @GetMapping
    @Operation(
        summary = "Get list of job posts",
        description = """
            Returns a paginated and filtered list of job posts. Supports comprehensive filtering and sorting options.
            
            ## Filtering Options
            - searchQuery: Search in title and description
            - location: Filter by job location (e.g., 'London', 'Remote')
            - jobType: Filter by job type (ID of job type, e.g., 1, 2, 3)
            - experience: Filter by experience (e.g., '3-5', '5+')
            - skillIds: Filter by required skill IDs (e.g., [1,2,3])
            
            ## Sorting Options
            - createdDateTime (default)
            - title
            - location
            - company.name
            
            ## Pagination
            - Default page size: 6 items
            - Page numbering starts from 0
            
            ## Example Requests
            1. Basic: GET /api/v1/job-posts
            2. With filters: GET /api/v1/job-posts?searchQuery=java&location=London&jobType=1
            3. With sorting: GET /api/v1/job-posts?sortBy=createdDateTime&sortDirection=DESC
            4. With pagination: GET /api/v1/job-posts?page=0&size=10
            
            ## Response Format
            ```json
            {
                "success": true,
                "message": "Job posts retrieved successfully",
                "data": {
                    "content": [
                        {
                            "id": 1,
                            "title": "Senior Java Developer",
                            "description": "We are looking for an experienced Java developer...",
                            "location": "London, UK",
                            "requirements": "- 5+ years of Java experience\\n- Spring Framework knowledge",
                            "company": {
                                "id": 1,
                                "name": "Tech Solutions Ltd",
                                "logo": "https://example.com/logo.png"
                            }
                        }
                    ],
                    "pageable": {
                        "pageNumber": 0,
                        "pageSize": 6,
                        "sort": {
                            "sorted": true,
                            "direction": "DESC",
                            "property": "createdDateTime"
                        }
                    },
                    "totalElements": 100,
                    "totalPages": 17,
                    "last": false,
                    "first": true,
                    "empty": false
                }
            }
            ```
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved list of job posts",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Page.class),
                examples = @ExampleObject(
                    value = """
                        {
                            "success": true,
                            "message": "Job posts retrieved successfully",
                            "data": {
                                "content": [
                                    {
                                        "id": 1,
                                        "title": "Senior Java Developer",
                                        "description": "We are looking for an experienced Java developer...",
                                        "location": "London, UK",
                                        "requirements": "- 5+ years of Java experience\\n- Spring Framework knowledge",
                                        "company": {
                                            "id": 1,
                                            "name": "Tech Solutions Ltd",
                                            "logo": "https://example.com/logo.png"
                                        }
                                    }
                                ],
                                "pageable": {
                                    "pageNumber": 0,
                                    "pageSize": 6,
                                    "sort": {
                                        "sorted": true,
                                        "direction": "DESC",
                                        "property": "createdDateTime"
                                    }
                                },
                                "totalElements": 100,
                                "totalPages": 17,
                                "last": false,
                                "first": true,
                                "empty": false
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                            "success": false,
                            "message": "Invalid sort field: invalidField",
                            "data": null
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<Page<JobPostResponseDTO>> getAllJobPosts(
        @Parameter(
            description = """
                Filter and sorting parameters:
                
                searchQuery: Search in title and description
                location: Filter by job location (e.g., 'London', 'Remote')
                jobType: Filter by job type (ID of job type, e.g., 1, 2, 3)
                experience: Filter by experience (e.g., '3-5', '5+')
                skillIds: Filter by required skill IDs (e.g., [1,2,3])
                sortBy: Field to sort by (createdDateTime, title, location, company.name)
                sortDirection: Sort direction (ASC, DESC)
                page: Page number (starts from 0)
                size: Number of items per page (default: 6)
                active: Filter by job post status (true/false)
                """,
            schema = @Schema(implementation = JobPostFilterRequestDTO.class),
            examples = {
                @ExampleObject(
                    name = "Basic filter",
                    value = "searchQuery=java&location=London"
                ),
                @ExampleObject(
                    name = "Advanced filter",
                    value = "jobType=1&experience=3-5&skillIds=1,2,3"
                ),
                @ExampleObject(
                    name = "Sorting and pagination",
                    value = "sortBy=createdDateTime&sortDirection=DESC&page=0&size=10"
                )
            }
        )
        JobPostFilterRequestDTO filterRequest,
        @RequestParam(defaultValue = "true") String active,
        Authentication authentication
    ) {
        log.info("[getAllJobPosts] Called with params: searchQuery={}, location={}, jobType={}, experience={}, skillIds={}, page={}, size={}, active={}",
            filterRequest.getSearchQuery(), filterRequest.getLocation(), filterRequest.getJobType(), filterRequest.getExperience(), filterRequest.getSkillIds(), filterRequest.getPage(), filterRequest.getSize(), active);
        try {
            Page<JobPostResponseDTO> result = jobPostService.getFilteredJobPosts(filterRequest, active);
            log.info("[getAllJobPosts] jobPostService.getFilteredJobPosts executed successfully");
            if (authentication != null && authentication.isAuthenticated()) {
                log.info("[getAllJobPosts] User is authenticated");
                if (filterRequest.getSearchQuery() != null) {
                    try {
                        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                        UserAccount user = userDetails.getUser();
                        log.info("[getAllJobPosts] Saving search history for userId={}", user.getId());
                        jobNotificationService.saveSearchHistory(
                            (long) user.getId(),
                            filterRequest.getSearchQuery(),
                            null, // tags
                            null, // skills
                            filterRequest.getJobType(),
                            filterRequest.getExperience() != null ? filterRequest.getExperience().toString() : null
                        );
                        log.info("[getAllJobPosts] Search history saved for userId={}", user.getId());
                    } catch (Exception e) {
                        log.warn("[getAllJobPosts] Could not save search history: {}", e.getMessage());
                    }
                }
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[getAllJobPosts] Exception: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get job post details",
        description = """
            Returns detailed information about a specific job post by its ID.
            
            ## Notes
            - If the user is authenticated, the view will be recorded in their history
            - The response includes detailed company information
            
            ## Response Format
            ```json
            {
                "success": true,
                "message": "Job post details retrieved successfully",
                "data": {
                    "id": 1,
                    "title": "Senior Java Developer",
                    "description": "We are looking for an experienced Java developer...",
                    "location": "London, UK",
                    "requirements": "- 5+ years of Java experience\\n- Spring Framework knowledge",
                    "company": {
                        "id": 1,
                        "name": "Tech Solutions Ltd",
                        "logo": "https://example.com/logo.png",
                        "description": "Leading technology solutions provider...",
                        "website": "https://techsolutions.com"
                    },
                    "jobType": "FULL_TIME",
                    "gradation": "SENIOR",
                    "salary": "50000-70000",
                    "createdDateTime": "2024-03-20T10:00:00",
                    "isActive": true
                }
            }
            ```
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved job post details",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JobPostDetailedResponseDTO.class),
                examples = @ExampleObject(
                    value = """
                        {
                            "success": true,
                            "message": "Job post details retrieved successfully",
                            "data": {
                                "id": 1,
                                "title": "Senior Java Developer",
                                "description": "We are looking for an experienced Java developer...",
                                "location": "London, UK",
                                "requirements": "- 5+ years of Java experience\\n- Spring Framework knowledge",
                                "company": {
                                    "id": 1,
                                    "name": "Tech Solutions Ltd",
                                    "logo": "https://example.com/logo.png",
                                    "description": "Leading technology solutions provider...",
                                    "website": "https://techsolutions.com"
                                },
                                "jobType": "FULL_TIME",
                                "gradation": "SENIOR",
                                "salary": "50000-70000",
                                "createdDateTime": "2024-03-20T10:00:00",
                                "isActive": true
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Job post not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                            "success": false,
                            "message": "Job post not found",
                            "data": null
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<?> getJobPostDetails(
        @Parameter(
            description = "Job post ID",
            required = true,
            example = "1"
        )
        @PathVariable Integer id,
        Authentication authentication
    ) {
        try {
            JobPostDetailedResponseDTO jobPost = jobPostService.getJobPostDetails(id);
            if (jobPost == null) {
                return ResponseEntity.status(404).body(
                    Map.of(
                        "success", false,
                        "message", "Job post not found",
                        "data", null
                    )
                );
            }
            // Save view history if user is authenticated
            if (authentication != null && authentication.isAuthenticated()) {
                try {
                    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                    UserAccount user = userDetails.getUser();
                    jobViewHistoryService.saveJobView(
                        (long) user.getId(),
                        (long) id,
                        jobPost.getTitle(),
                        jobPost.getCompany().getName()
                    );
                } catch (Exception e) {
                    log.error("[getJobPostDetails] Error while saving view history: {}", e.getMessage(), e);
                }
            }
            return ResponseEntity.ok(jobPost);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(
                Map.of(
                    "success", false,
                    "message", "Job post not found",
                    "data", null
                )
            );
        } catch (Exception e) {
            log.error("[getJobPostDetails] Exception: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(
                Map.of(
                    "success", false,
                    "message", "Internal server error",
                    "data", null
                )
            );
        }
    }

    // --- СПРАВОЧНИКИ ---
    @Operation(
        summary = "Get all tags",
        description = "Returns all available tags for job posts. Useful for job creation forms.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "List of tags",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TagDTO.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          \"success\": true,
                          \"message\": \"Tags retrieved successfully\",
                          \"data\": [
                            {\"id\":1,\"name\":\"Java\"},
                            {\"id\":2,\"name\":\"Spring\"}
                          ]
                        }
                        """
                    )
                )
            )
        }
    )
    @GetMapping("/tags")
    public ResponseEntity<List<TagDTO>> getAllTags() {
        return ResponseEntity.ok(
            tagRepository.findAll().stream()
                .map(tag -> TagDTO.builder().id(tag.getId()).name(tag.getName()).build())
                .toList()
        );
    }

    @Operation(
        summary = "Get all skills",
        description = "Returns all available skills for job posts. Useful for job creation forms.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "List of skills",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = JobPostSkillDTO.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          \"success\": true,
                          \"message\": \"Skills retrieved successfully\",
                          \"data\": [
                            {\"id\":1,\"name\":\"Java\"},
                            {\"id\":2,\"name\":\"SQL\"}
                          ]
                        }
                        """
                    )
                )
            )
        }
    )
    @GetMapping("/skills")
    public ResponseEntity<List<JobPostSkillDTO>> getAllSkills() {
        return ResponseEntity.ok(
            skillRepository.findAll().stream()
                .map(skill -> JobPostSkillDTO.builder().id(skill.getId()).name(skill.getName()).build())
                .toList()
        );
    }

    @Operation(
        summary = "Get all gradations",
        description = "Returns all available job gradations (levels). Useful for job creation forms.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "List of gradations",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = JobGradationDTO.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          \"success\": true,
                          \"message\": \"Gradations retrieved successfully\",
                          \"data\": [
                            {\"id\":1,\"name\":\"Junior\"},
                            {\"id\":2,\"name\":\"Senior\"}
                          ]
                        }
                        """
                    )
                )
            )
        }
    )
    @GetMapping("/gradations")
    public ResponseEntity<List<JobGradationDTO>> getAllGradations() {
        return ResponseEntity.ok(
            jobGradationRepository.findAll().stream()
                .map(grad -> new JobGradationDTO(grad.getId(), grad.getName()))
                .toList()
        );
    }

    @Operation(
        summary = "Get all job types",
        description = "Returns all available job types. Useful for job creation forms.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "List of job types",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = JobTypeDTO.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          \"success\": true,
                          \"message\": \"Job types retrieved successfully\",
                          \"data\": [
                            {\"id\":1,\"name\":\"Full Time\"},
                            {\"id\":2,\"name\":\"Part Time\"}
                          ]
                        }
                        """
                    )
                )
            )
        }
    )
    @GetMapping("/types")
    public ResponseEntity<List<JobTypeDTO>> getAllJobTypes() {
        return ResponseEntity.ok(
            jobTypeRepository.findAll().stream()
                .map(type -> new JobTypeDTO(type.getId(), type.getName()))
                .toList()
        );
    }

    @Operation(
        summary = "Get all required experiences",
        description = "Returns all available required experiences. Useful for job creation forms.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "List of required experiences",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RequiredExperienceDTO.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          \"success\": true,
                          \"message\": \"Experiences retrieved successfully\",
                          \"data\": [
                            {\"id\":1,\"experience\":\"1-3 years\"},
                            {\"id\":2,\"experience\":\"3-5 years\"}
                          ]
                        }
                        """
                    )
                )
            )
        }
    )
    @GetMapping("/experiences")
    public ResponseEntity<List<RequiredExperienceDTO>> getAllExperiences() {
        return ResponseEntity.ok(
            requiredExperienceRepository.findAll().stream()
                .map(exp -> RequiredExperienceDTO.builder().id(exp.getId()).experience(exp.getExperience()).build())
                .toList()
        );
    }

    // --- СОЗДАНИЕ ВАКАНСИИ ---
    @Operation(
        summary = "Create a new job post",
        description = "Creates a new job post for the authenticated company. Only users with COMPANY role can create vacancies.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                schema = @Schema(implementation = JobPostCreateRequestDTO.class),
                examples = @ExampleObject(
                    value = """
                    {
                      \"title\": \"Senior Java Developer\",
                      \"titleEn\": \"Senior Java Developer\",
                      \"description\": \"We are looking for an experienced Java developer...\",
                      \"descriptionEn\": \"We are looking for an experienced Java developer...\",
                      \"requirements\": [\"5+ years of Java experience\", \"Spring Framework knowledge\"],
                      \"responsibilities\": [\"Develop backend services\", \"Participate in code reviews\"],
                      \"salaryRange\": \"$80,000 - $120,000\",
                      \"location\": \"London, UK\",
                      \"jobTypeId\": 1,
                      \"jobGradationId\": 2,
                      \"requiredExperienceId\": 3,
                      \"tagIds\": [1,2,3],
                      \"skills\": [{\"id\":1,\"level\":3},{\"id\":2,\"level\":2}],
                      \"language\": \"English\"
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Job post created successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ 'success': true, 'message': 'Job post created', 'data': { 'id': 123 } }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid input or reference ID not found",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ 'success': false, 'message': 'Tag not found', 'data': null }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Only COMPANY users can create job posts"
            )
        }
    )
    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> createJobPost(@Valid @RequestBody JobPostCreateRequestDTO request,
                                           Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserAccount user = userDetails.getUser();
        if (!user.getUserType().getName().equalsIgnoreCase("COMPANY")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "success", false,
                "message", "Only COMPANY users can create job posts",
                "data", null
            ));
        }
        // Получаем справочные сущности по id
        JobType jobType = jobTypeRepository.findById(request.getJobTypeId())
                .orElse(null);
        if (jobType == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Job type not found");
            error.put("data", null);
            return ResponseEntity.badRequest().body(error);
        }
        JobGradation gradation = jobGradationRepository.findById(request.getJobGradationId())
                .orElse(null);
        if (gradation == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Job gradation not found");
            error.put("data", null);
            return ResponseEntity.badRequest().body(error);
        }
        RequiredExperience experience = requiredExperienceRepository.findById(request.getRequiredExperienceId())
                .orElse(null);
        if (experience == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Required experience not found");
            error.put("data", null);
            return ResponseEntity.badRequest().body(error);
        }
        List<Tag> tags = tagRepository.findAllById(request.getTagIds());
        if (tags.size() != request.getTagIds().size()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Some tags not found");
            error.put("data", null);
            return ResponseEntity.badRequest().body(error);
        }
        // Создаём JobPost
        JobPost jobPost = new JobPost();
        jobPost.setTitle(request.getTitle());
        jobPost.setTitleEn(request.getTitleEn());
        jobPost.setJobDescription(request.getDescription());
        jobPost.setJobDescriptionEn(request.getDescriptionEn());
        jobPost.setJobLocation(request.getLocation());
        jobPost.setSalary(request.getSalaryRange());
        jobPost.setLanguage(request.getLanguage());
        jobPost.setJobType(jobType);
        jobPost.setJobGradation(gradation);
        jobPost.setExperience(experience);
        jobPost.setCompany(user.getCompany());
        jobPost.setCreatedDateTime(new java.util.Date());
        jobPost.setActive(true);
        jobPost.setTags(new java.util.LinkedHashSet<Tag>(tags));
        // Сохраняем JobPost (без скиллов)
        jobPost = jobPostService.save(jobPost);
        // Привязываем скиллы
        for (JobPostSkillDTO skillDTO : request.getSkills()) {
            Skill skill = skillRepository.findById(skillDTO.getId()).orElse(null);
            if (skill == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Skill not found: " + skillDTO.getId());
                error.put("data", null);
                return ResponseEntity.badRequest().body(error);
            }
            JobPostSkill jobPostSkill = new JobPostSkill();
            jobPostSkill.setJobPost(jobPost);
            jobPostSkill.setSkill(skill);
            jobPostSkill.setSkillLevel(skillDTO.getLevel());
            jobPostSkillRepository.save(jobPostSkill);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", jobPost.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Job post created");
        response.put("data", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Get job posts by company ID",
        description = "Returns a paginated list of job posts for a specific company. Default page size is 6. You can change the page and size via query params.",
        parameters = {
            @Parameter(name = "companyId", description = "ID of the company", required = true, example = "1"),
            @Parameter(name = "page", description = "Page number (starts from 0)", example = "0", required = false),
            @Parameter(name = "size", description = "Number of items per page", example = "6", required = false),
            @Parameter(name = "active", description = "Filter by job post status (true/false)", example = "true", required = false)
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved job posts",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Company not found"
            )
        }
    )
    @GetMapping("/by-company/{companyId}")
    public ResponseEntity<Page<JobPostResponseDTO>> getJobPostsByCompany(
            @PathVariable Integer companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "true") String active
    ) {
        Page<JobPost> jobPosts = jobPostService.getJobPostsByCompanyId(companyId, page, size, active);
        Page<JobPostResponseDTO> response = jobPosts.map(jobPostService.getJobPostMapper()::toResponseDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Respond to a job post (apply)",
        description = "Allows an authenticated user (seeker) to apply to a job post. Vacancy id is passed in the request body.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                schema = @Schema(implementation = RespondRequest.class),
                examples = @ExampleObject(value = "{\"jobPostId\": 123}")
            )
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Successfully applied to job post"),
            @ApiResponse(responseCode = "400", description = "Invalid job post or user"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
        }
    )
    @PostMapping("/apply")
    public ResponseEntity<?> respondToJobPost(@RequestBody RespondRequest request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized", "data", null));
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserAccount user = userDetails.getUser();
        Seeker seeker = seekerRepository.findByUserAccountId(user.getId());
        if (seeker == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User is not a seeker", "data", null));
        }
        JobPost jobPost = jobPostService.getJobPostById(request.getJobPostId());
        if (jobPost == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Job post not found", "data", null));
        }
        JobPostActivity activity = new JobPostActivity();
        activity.setJobPost(jobPost);
        activity.setSeeker(seeker);
        activity.setApplyDate(new java.util.Date());
        activity.setStatus("Очікує");
        jobPostActivityRepository.save(activity);
        return ResponseEntity.status(201).body(Map.of("success", true, "message", "Successfully applied to job post"));
    }

    public static class RespondRequest {
        @Schema(description = "Job post ID", example = "123")
        private Integer jobPostId;
        public Integer getJobPostId() { return jobPostId; }
        public void setJobPostId(Integer jobPostId) { this.jobPostId = jobPostId; }
    }

    @Operation(
        summary = "Get all job posts for authenticated company",
        description = "Returns paginated list of job posts for the authenticated company. Only for COMPANY users.",
        parameters = {
            @Parameter(name = "page", description = "Page number (starts from 0)", example = "0", required = false),
            @Parameter(name = "size", description = "Number of items per page", example = "6", required = false),
            @Parameter(name = "active", description = "Filter by job post status (true/false)", example = "true", required = false)
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved job posts")
        }
    )
    @GetMapping("/my")
    public ResponseEntity<?> getMyJobPosts(Authentication authentication,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "6") int size,
                                           @RequestParam(defaultValue = "true") String active) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserAccount user = userDetails.getUser();
        if (!user.getUserType().getName().equalsIgnoreCase("COMPANY") || user.getCompany() == null) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Only COMPANY users can view their job posts", "data", null));
        }
        var result = jobPostService.getCompanyJobPosts(user.getCompany().getId(), page, size, active);
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }

    @Operation(
        summary = "Get full job post info for company",
        description = "Returns full info about a job post for the authenticated company (must be owner).",
        parameters = {
            @Parameter(name = "jobPostId", description = "Job post ID", required = true, example = "123")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved job post details"),
            @ApiResponse(responseCode = "404", description = "Job post not found or not owned by company")
        }
    )
    @GetMapping("/my/{jobPostId}")
    public ResponseEntity<?> getMyJobPostDetails(@PathVariable Integer jobPostId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserAccount user = userDetails.getUser();
        if (!user.getUserType().getName().equalsIgnoreCase("COMPANY") || user.getCompany() == null) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Only COMPANY users can view their job posts", "data", null));
        }
        var details = jobPostService.getCompanyJobPostDetails(user.getCompany().getId(), jobPostId);
        if (details == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Job post not found or not owned by company", "data", null));
        }
        return ResponseEntity.ok(Map.of("success", true, "data", details));
    }

    @Operation(
        summary = "Get all applicants for a job post",
        description = "Returns all applicants for a job post (only for company-owner).",
        parameters = {
            @Parameter(name = "jobPostId", description = "Job post ID", required = true, example = "123")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved applicants"),
            @ApiResponse(responseCode = "404", description = "Job post not found or not owned by company")
        }
    )
    @GetMapping("/{jobPostId}/applicants")
    public ResponseEntity<?> getApplicantsForJobPost(@PathVariable Integer jobPostId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserAccount user = userDetails.getUser();
        if (!user.getUserType().getName().equalsIgnoreCase("COMPANY") || user.getCompany() == null) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Only COMPANY users can view applicants", "data", null));
        }
        var applicants = jobPostService.getApplicantsForJobPost(user.getCompany().getId(), jobPostId);
        return ResponseEntity.ok(Map.of("success", true, "data", applicants));
    }

    @Operation(
        summary = "Update job application (JobPostActivity) status",
        description = "Allows an authenticated COMPANY user to update the status of a job application (JobPostActivity) by its id. Only the company-owner of the vacancy can update.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                schema = @Schema(implementation = JobPostActivityStatusUpdateRequestDTO.class),
                examples = @ExampleObject(value = "{\"activityId\": 123, \"status\": \"Reviewed\"}")
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or not allowed"),
            @ApiResponse(responseCode = "403", description = "Only COMPANY users can update status")
        }
    )
    @PostMapping("/activity/update-status")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<StandardResponseDTO<Void>> updateJobPostActivityStatus(
            @RequestBody JobPostActivityStatusUpdateRequestDTO request,
            Authentication authentication) {
        try {
            log.info("[updateJobPostActivityStatus] Request: activityId={}, status={}", request.getActivityId(), request.getStatus());
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("[updateJobPostActivityStatus] Unauthorized: authentication is null or not authenticated");
                return ResponseEntity.status(401).body(StandardResponseDTO.error("Unauthorized"));
            }
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UserAccount user = userDetails.getUser();
            log.info("[updateJobPostActivityStatus] User: id={}, email={}, userType={}, company={}",
                    user.getId(), user.getEmail(),
                    user.getUserType() != null ? user.getUserType().getName() : null,
                    user.getCompany() != null ? user.getCompany().getId() : null);
            if (user.getUserType() == null) {
                log.error("[updateJobPostActivityStatus] UserType is null for user id={}", user.getId());
                return ResponseEntity.status(500).body(StandardResponseDTO.error("User type is null"));
            }
            if (!user.getUserType().getName().equalsIgnoreCase("COMPANY") || user.getCompany() == null) {
                log.warn("[updateJobPostActivityStatus] Forbidden: not a company or company is null");
                return ResponseEntity.status(403).body(StandardResponseDTO.error("Only COMPANY users can update status"));
            }
            boolean updated = jobPostService.updateJobPostActivityStatus(
                    request.getActivityId(),
                    request.getStatus(),
                    user.getCompany().getId()
            );
            log.info("[updateJobPostActivityStatus] Update result: {}", updated);
            if (updated) {
                return ResponseEntity.ok(StandardResponseDTO.success(null));
            } else {
                log.warn("[updateJobPostActivityStatus] Not found or not allowed: activityId={}, companyId={}",
                        request.getActivityId(), user.getCompany().getId());
                return ResponseEntity.status(400).body(StandardResponseDTO.error("Not found or not allowed"));
            }
        } catch (Exception e) {
            log.error("[updateJobPostActivityStatus] Exception: ", e);
            return ResponseEntity.status(500).body(StandardResponseDTO.error("Internal server error: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Delete a job post (only for company-owner)",
        description = "Allows an authenticated COMPANY user to delete their own job post by id. Only the company-owner can delete.",
        parameters = {
            @Parameter(name = "jobPostId", description = "Job post ID", required = true, example = "123")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Job post deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Job post not found or not owned by company"),
            @ApiResponse(responseCode = "403", description = "Only COMPANY users can delete job posts")
        }
    )
    @DeleteMapping("/{jobPostId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<StandardResponseDTO<Void>> deleteJobPost(
            @PathVariable Integer jobPostId,
            Authentication authentication) {
        try {
            log.info("[deleteJobPost] Request: jobPostId={}", jobPostId);
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("[deleteJobPost] Unauthorized: authentication is null or not authenticated");
                return ResponseEntity.status(401).body(StandardResponseDTO.error("Unauthorized"));
            }
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UserAccount user = userDetails.getUser();
            log.info("[deleteJobPost] User: id={}, email={}, userType={}, company={}",
                    user.getId(), user.getEmail(),
                    user.getUserType() != null ? user.getUserType().getName() : null,
                    user.getCompany() != null ? user.getCompany().getId() : null);
            if (user.getUserType() == null || !user.getUserType().getName().equalsIgnoreCase("COMPANY") || user.getCompany() == null) {
                log.warn("[deleteJobPost] Forbidden: not a company or company is null");
                return ResponseEntity.status(403).body(StandardResponseDTO.error("Only COMPANY users can delete job posts"));
            }
            boolean deleted = jobPostService.deleteJobPostByCompany(jobPostId, user.getCompany().getId());
            log.info("[deleteJobPost] Delete result: {}", deleted);
            if (deleted) {
                return ResponseEntity.ok(StandardResponseDTO.success(null));
            } else {
                log.warn("[deleteJobPost] Not found or not allowed: jobPostId={}, companyId={}", jobPostId, user.getCompany().getId());
                return ResponseEntity.status(404).body(StandardResponseDTO.error("Job post not found or not owned by company"));
            }
        } catch (Exception e) {
            log.error("[deleteJobPost] Exception: ", e);
            return ResponseEntity.status(500).body(StandardResponseDTO.error("Internal server error: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Hard delete a job post (only for company-owner)",
        description = "Allows an authenticated COMPANY user to hard delete their own job post by id. Only the company-owner can delete. This will permanently remove the job post.",
        parameters = {
            @Parameter(name = "jobPostId", description = "Job post ID", required = true, example = "123")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Job post hard deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Job post not found or not owned by company"),
            @ApiResponse(responseCode = "403", description = "Only COMPANY users can delete job posts")
        }
    )
    @DeleteMapping("/{jobPostId}/hard")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<StandardResponseDTO<Void>> hardDeleteJobPost(
            @PathVariable Integer jobPostId,
            Authentication authentication) {
        try {
            log.info("[hardDeleteJobPost] Request: jobPostId={}", jobPostId);
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("[hardDeleteJobPost] Unauthorized: authentication is null or not authenticated");
                return ResponseEntity.status(401).body(StandardResponseDTO.error("Unauthorized"));
            }
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UserAccount user = userDetails.getUser();
            log.info("[hardDeleteJobPost] User: id={}, email={}, userType={}, company={}",
                    user.getId(), user.getEmail(),
                    user.getUserType() != null ? user.getUserType().getName() : null,
                    user.getCompany() != null ? user.getCompany().getId() : null);
            if (user.getUserType() == null || !user.getUserType().getName().equalsIgnoreCase("COMPANY") || user.getCompany() == null) {
                log.warn("[hardDeleteJobPost] Forbidden: not a company or company is null");
                return ResponseEntity.status(403).body(StandardResponseDTO.error("Only COMPANY users can delete job posts"));
            }
            boolean deleted = jobPostService.hardDeleteJobPostByCompany(jobPostId, user.getCompany().getId());
            log.info("[hardDeleteJobPost] Delete result: {}", deleted);
            if (deleted) {
                return ResponseEntity.ok(StandardResponseDTO.success(null));
            } else {
                log.warn("[hardDeleteJobPost] Not found or not allowed: jobPostId={}, companyId={}", jobPostId, user.getCompany().getId());
                return ResponseEntity.status(404).body(StandardResponseDTO.error("Job post not found or not owned by company"));
            }
        } catch (Exception e) {
            log.error("[hardDeleteJobPost] Exception: ", e);
            return ResponseEntity.status(500).body(StandardResponseDTO.error("Internal server error: " + e.getMessage()));
        }
    }
}
