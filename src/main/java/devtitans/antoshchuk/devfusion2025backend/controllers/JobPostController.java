package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.request.JobPostFilterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostDetailedResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.services.JobPostService;
import devtitans.antoshchuk.devfusion2025backend.services.JobViewHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/job-posts")
@Tag(
    name = "Job Posts",
    description = """
        API for managing job posts - search, filtering, and details retrieval.
        
        ## Authentication
        Some endpoints require a valid JWT token in the Authorization header:
        ```
        Authorization: Bearer <your_jwt_token>
        ```
        
        ## Error Responses
        All endpoints return standardized error responses in the following format:
        ```json
        {
            "success": false,
            "message": "Error description",
            "data": null
        }
        ```
        """
)
public class JobPostController {

    private final JobPostService jobPostService;
    private final JobViewHistoryService jobViewHistoryService;

    @Autowired
    public JobPostController(JobPostService jobPostService, JobViewHistoryService jobViewHistoryService) {
        this.jobPostService = jobPostService;
        this.jobViewHistoryService = jobViewHistoryService;
    }

    @GetMapping
    @Operation(
        summary = "Get list of job posts",
        description = """
            Returns a paginated and filtered list of job posts. Supports comprehensive filtering and sorting options.
            
            ## Filtering Options
            - searchQuery: Search in title and description
            - location: Filter by job location (e.g., 'London', 'Remote')
            - companyId: Filter by specific company
            - jobType: Filter by job type (FULL_TIME, PART_TIME, CONTRACT, FREELANCE, INTERNSHIP)
            - gradation: Filter by experience level (JUNIOR, MIDDLE, SENIOR, LEAD)
            - isActive: Filter by vacancy status (true/false)
            
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
            2. With filters: GET /api/v1/job-posts?searchQuery=java&location=London&jobType=FULL_TIME
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
                companyId: Filter by specific company
                jobType: Filter by job type (FULL_TIME, PART_TIME, CONTRACT, FREELANCE, INTERNSHIP)
                gradation: Filter by experience level (JUNIOR, MIDDLE, SENIOR, LEAD)
                isActive: Filter by vacancy status (true/false)
                sortBy: Field to sort by (createdDateTime, title, location, company.name)
                sortDirection: Sort direction (ASC, DESC)
                page: Page number (starts from 0)
                size: Number of items per page (default: 6)
                """,
            schema = @Schema(implementation = JobPostFilterRequestDTO.class),
            examples = {
                @ExampleObject(
                    name = "Basic filter",
                    value = "searchQuery=java&location=London"
                ),
                @ExampleObject(
                    name = "Advanced filter",
                    value = "jobType=FULL_TIME&gradation=SENIOR&isActive=true"
                ),
                @ExampleObject(
                    name = "Sorting and pagination",
                    value = "sortBy=createdDateTime&sortDirection=DESC&page=0&size=10"
                )
            }
        )
        JobPostFilterRequestDTO filterRequest
    ) {
        return ResponseEntity.ok(jobPostService.getFilteredJobPosts(filterRequest));
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
    public ResponseEntity<JobPostDetailedResponseDTO> getJobPostDetails(
        @Parameter(
            description = "Job post ID",
            required = true,
            example = "1"
        )
        @PathVariable Integer id,
        Authentication authentication
    ) {
        JobPostDetailedResponseDTO jobPost = jobPostService.getJobPostDetails(id);
        
        // Save view history if user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            UserAccount user = (UserAccount) authentication.getPrincipal();
            jobViewHistoryService.saveJobView(
                (long) user.getId(),
                (long) id,
                jobPost.getTitle(),
                jobPost.getCompany().getName()
            );
        }
        
        return ResponseEntity.ok(jobPost);
    }

    @GetMapping("/search")
    @Operation(
        summary = "Search job posts",
        description = "Search job posts with filters and save search history for authenticated users"
    )
    public ResponseEntity<Page<JobPostResponseDTO>> searchJobPosts(
        @RequestBody JobPostFilterRequestDTO filterRequest,
        Authentication authentication
    ) {
        Page<JobPostResponseDTO> result = jobPostService.getFilteredJobPosts(filterRequest);
        
        // Save search history if user is authenticated
        if (authentication != null && authentication.isAuthenticated() && filterRequest.getSearchQuery() != null) {
            UserAccount user = (UserAccount) authentication.getPrincipal();
            jobViewHistoryService.saveJobView(
                (long) user.getId(),
                null,
                filterRequest.getSearchQuery(),
                null
            );
        }
        
        return ResponseEntity.ok(result);
    }
}
