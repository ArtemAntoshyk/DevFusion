package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyBaseResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostDetailedResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.services.CompanyService;
import devtitans.antoshchuk.devfusion2025backend.services.JobPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Job Posts", description = "Endpoints for managing job posts and retrieving job-related information")
public class JobPostController {
    private final JobPostService jobPostService;
    private final CompanyService companyService;

    @Autowired
    public JobPostController(JobPostService jobPostService, CompanyService companyService) {
        this.jobPostService = jobPostService;
        this.companyService = companyService;
    }

    @Operation(
        summary = "Get all job posts with pagination",
        description = "Retrieves a list of all job posts with basic company information, paginated for better performance"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the list of job posts",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CompanyBaseResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid pagination parameters",
            content = @Content(schema = @Schema(hidden = true))
        )
    })
    @GetMapping("/all")
    public ResponseEntity<List<CompanyBaseResponseDTO>> getAllJobPosts(
        @Parameter(description = "Page number (zero-based)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        
        @Parameter(description = "Number of items per page", example = "10")
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(companyService.getAllCompaniesBaseInfoDTOs());
    }

    @Operation(
        summary = "Get detailed job post information",
        description = "Returns comprehensive information about a specific job post including company details, " +
                     "location, employment type, required experience, and other relevant details"
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
                    example = "{\"error\":\"Not Found\",\"message\":\"Вакансію не знайдено\"}"
                )
            )
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<JobPostDetailedResponseDTO> getJobPostDetails(
        @Parameter(description = "ID of the job post to retrieve", required = true, example = "1")
        @PathVariable Integer id
    ) {
        JobPostDetailedResponseDTO jobPost = jobPostService.getJobPostDetails(id);
        return ResponseEntity.ok(jobPost);
    }
}
