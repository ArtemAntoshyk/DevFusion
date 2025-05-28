package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.request.JobPostFilterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostDetailedResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.services.JobPostService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/job-posts")
@Tag(name = "Job Posts", description = "API for managing job posts - search, filtering, and details retrieval")
public class JobPostController {

    private final JobPostService jobPostService;

    @Autowired
    public JobPostController(JobPostService jobPostService) {
        this.jobPostService = jobPostService;
    }

    @GetMapping
    @Operation(
        summary = "Get list of job posts",
        description = """
            Returns a paginated and filtered list of job posts. Supports comprehensive filtering and sorting options.
            
            Filtering options:
            - Search by title and description
            - Filter by location
            - Filter by company
            - Filter by job type (FULL_TIME, PART_TIME, CONTRACT, etc.)
            - Filter by experience level (JUNIOR, MIDDLE, SENIOR)
            - Filter by active status
            
            Sorting options:
            - createdDateTime (default)
            - title
            - location
            - company.name
            
            Pagination:
            - Default page size: 6 items
            - Page numbering starts from 0
            
            Example requests:
            1. Basic: GET /api/v1/job-posts
            2. With filters: GET /api/v1/job-posts?searchQuery=java&location=London&jobType=FULL_TIME
            3. With sorting: GET /api/v1/job-posts?sortBy=createdDateTime&sortDirection=DESC
            4. With pagination: GET /api/v1/job-posts?page=0&size=10
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved list of job posts",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Page.class),
                examples = @ExampleObject(value = """
                    {
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
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = "{\"error\":\"Bad Request\",\"message\":\"Invalid sort field: invalidField\"}"
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
        description = "Returns detailed information about a specific job post by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved job post details",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JobPostDetailedResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Job post not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = "{\"error\":\"Not Found\",\"message\":\"Job post not found\"}"
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
        @PathVariable Integer id
    ) {
        return ResponseEntity.ok(jobPostService.getJobPostDetails(id));
    }
}
