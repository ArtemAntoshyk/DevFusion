package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.response.SeekerJobPostResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.services.JobPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import devtitans.antoshchuk.devfusion2025backend.security.detail.CustomUserDetails;
import devtitans.antoshchuk.devfusion2025backend.repositories.SeekerRepository;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import devtitans.antoshchuk.devfusion2025backend.dto.response.HasAppliedResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/v1/job-posts")
@RequiredArgsConstructor
public class JobSeekerController {
    private final JobPostService jobPostService;
    private final SeekerRepository seekerRepository;

    /**
     * Get all job posts the current seeker has applied to (paginated, sorted by most recent application).
     * <p>
     * Requires authentication via Bearer token. Returns a paginated list of job posts (id, title, company name, status, application date)
     * that the authenticated seeker has applied to, sorted from the most recent application to the oldest.
     * </p>
     *
     * <b>Authentication:</b> Bearer JWT token required in the Authorization header.<br>
     * <b>Pagination:</b> Use 'page' and 'size' query parameters.<br>
     * <b>Sorting:</b> Always sorted by application date (descending).
     *
     * <b>Response example:</b>
     * <pre>
     * {
     *   "content": [
     *     {
     *       "jobPostId": 123,
     *       "title": "Java Developer",
     *       "companyName": "Acme Corp",
     *       "status": "Pending",
     *       "applyDate": "2024-06-01T12:34:56.000+00:00"
     *     }
     *   ],
     *   "pageable": { ... },
     *   "totalElements": 1,
     *   ...
     * }
     * </pre>
     */
    @Operation(
        summary = "Get all job posts the seeker has applied to (paginated)",
        description = "Returns a paginated list of job posts (id, title, company name, status, application date) that the authenticated seeker has applied to. Sorted by most recent application first.",
        parameters = {
            @Parameter(name = "page", description = "Page number (zero-based)", example = "0"),
            @Parameter(name = "size", description = "Page size", example = "10")
        },
        security = {@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully returned applied job posts"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token")
    })
    @GetMapping("/applied")
    public ResponseEntity<Page<SeekerJobPostResponseDTO>> getAppliedJobPosts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        int seekerId = extractSeekerIdFromUserDetails(userDetails);
        return ResponseEntity.ok(jobPostService.getSeekerAppliedJobPosts(seekerId, page, size));
    }

    /**
     * Check if the current seeker has applied to a specific job post and get the application date.
     * <p>
     * Requires authentication via Bearer token. Returns <code>hasApplied=true</code> and the application date if the seeker has applied to the job post,
     * otherwise <code>hasApplied=false</code> and a null date. If the user is not authenticated, always returns <code>hasApplied=false</code>.
     * </p>
     *
     * <b>Authentication:</b> Bearer JWT token required in the Authorization header.<br>
     * <b>Response example (applied):</b>
     * <pre>
     * {
     *   "hasApplied": true,
     *   "applyDate": "2024-06-01T12:34:56.000+00:00"
     * }
     * </pre>
     * <b>Response example (not applied):</b>
     * <pre>
     * {
     *   "hasApplied": false,
     *   "applyDate": null
     * }
     * </pre>
     */
    @Operation(
        summary = "Check if the seeker has applied to a job post",
        description = "Returns true and the application date if the authenticated seeker has applied to the specified job post, otherwise false and a null date.",
        parameters = {
            @Parameter(name = "jobPostId", description = "Job post ID", required = true, example = "123")
        },
        security = {@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Application status returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token")
    })
    @GetMapping("/{jobPostId}/has-applied")
    public ResponseEntity<HasAppliedResponseDTO> hasAppliedToJobPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer jobPostId
    ) {
        if (userDetails == null) {
            return ResponseEntity.ok(new HasAppliedResponseDTO(false, null));
        }
        int seekerId = extractSeekerIdFromUserDetails(userDetails);
        var res = jobPostService.hasSeekerAppliedToJobPost(seekerId, jobPostId);
        return ResponseEntity.ok(new HasAppliedResponseDTO(res.isHasApplied(), res.getApplyDate()));
    }

    private int extractSeekerIdFromUserDetails(UserDetails userDetails) {
        if (!(userDetails instanceof CustomUserDetails cud)) {
            throw new IllegalArgumentException("Invalid user details");
        }
        int userId = cud.getId();
        var seeker = seekerRepository.findByUserAccountId(userId);
        if (seeker == null) throw new IllegalStateException("Seeker profile not found");
        return seeker.getId();
    }
} 