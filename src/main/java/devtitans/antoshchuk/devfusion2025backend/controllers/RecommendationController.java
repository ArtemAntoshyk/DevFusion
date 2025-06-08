package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.request.RecommendationRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.RecommendationResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import devtitans.antoshchuk.devfusion2025backend.services.RecommendationService;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.JobPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/recommend")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final JobPostMapper jobPostMapper;

    @PostMapping
    @Operation(
        summary = "Get job recommendations",
        description = """
            Universal endpoint for job recommendations. Supports 4 scenarios:\n\n
            1. **Search without filters** — only the `query` field (e.g., \"Java developer\").
            2. **Search with filters** — `query` + `tagIds`/`categoryIds` (e.g., \"Java developer\", tags: Backend, Java).
            3. **Recommendations for a job post** — only `jobPostId` (e.g., recommendations for job post with id=123).
            4. **Recommendations for the last viewed job post** — only `lastViewedJobPostId` (e.g., recommendations for the last viewed job post).

            Returns a list of jobs (JobPostResponseDTO) with pagination.
        """,
        requestBody = @RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = devtitans.antoshchuk.devfusion2025backend.dto.request.RecommendationRequestDTO.class),
                examples = {
                    @ExampleObject(
                        name = "Search without filters",
                        value = "{\n  \"query\": \"Java developer\"\n}"
                    ),
                    @ExampleObject(
                        name = "Search with filters",
                        value = "{\n  \"query\": \"Java developer\",\n  \"tagIds\": [1,2]\n}"
                    ),
                    @ExampleObject(
                        name = "Recommendations for a job post",
                        value = "{\n  \"jobPostId\": 123\n}"
                    ),
                    @ExampleObject(
                        name = "Recommendations for the last viewed job post",
                        value = "{\n  \"lastViewedJobPostId\": 456\n}"
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "List of recommended jobs",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = devtitans.antoshchuk.devfusion2025backend.dto.response.RecommendationResponseDTO.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          \"jobs\": [
                            {
                              \"id\": 1,
                              \"title\": \"Senior Java Developer\",
                              \"titleEn\": \"Senior Java Developer\",
                              \"description\": \"We are looking for an experienced Java developer...\",
                              \"descriptionEn\": \"We are looking for an experienced Java developer...\",
                              \"location\": \"London, UK\",
                              \"salaryRange\": \"$80,000 - $120,000\",
                              \"employmentType\": \"Full-time\",
                              \"experienceLevel\": \"Senior\",
                              \"requiredExperience\": {\"id\":3,\"experience\":\"3-5 years\"},
                              \"tags\": [{\"id\":1,\"name\":\"Java\"}],
                              \"skills\": [{\"id\":1,\"name\":\"Java\",\"level\":3}],
                              \"language\": \"English\",
                              \"company\": {\"id\":1,\"name\":\"Tech Solutions Inc.\",\"logo\":\"https://example.com/logo.png\"},
                              \"createdAt\": \"2024-06-01T12:00:00.000+00:00\",
                              \"isActive\": true
                            }
                          ],
                          \"page\": 0,
                          \"size\": 10,
                          \"total\": 1
                        }
                        """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request or parameters"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        }
    )
    public RecommendationResponseDTO recommend(@RequestBody RecommendationRequestDTO req) {
        String query = req.getQuery();
        Integer topK = req.getTopK();
        Double threshold = req.getThreshold();
        List<Integer> allowedIds = null;

        // Сценарий 3: рекомендации к вакансии
        if (req.getJobPostId() != null) {
            query = recommendationService.getJobTitleById(req.getJobPostId());
            topK = (topK != null) ? topK : 3;
            allowedIds = new ArrayList<>();
        }
        // Сценарий 4: рекомендации к последней просмотренной
        else if (req.getLastViewedJobPostId() != null) {
            query = recommendationService.getJobTitleById(req.getLastViewedJobPostId());
            allowedIds = new ArrayList<>();
        }
        // Сценарий 2: поиск с фильтрами
        else if ((req.getCategoryIds() != null && !req.getCategoryIds().isEmpty()) || (req.getTagIds() != null && !req.getTagIds().isEmpty())) {
            allowedIds = recommendationService.getAllowedIds(req.getCategoryIds(), req.getTagIds());
        }
        // Сценарий 1: поиск без фильтров
        else {
            allowedIds = new ArrayList<>();
        }

        List<Integer> recommendedIds = recommendationService.getRecommendedIds(query, topK, threshold, allowedIds);
        List<JobPost> posts = recommendationService.getJobPostsByIds(recommendedIds);
        List<JobPostResponseDTO> dtos = posts.stream().map(jobPostMapper::toResponseDTO).toList();

        // Пагинация
        int page = req.getPage() != null ? req.getPage() : 0;
        int size = req.getSize() != null ? req.getSize() : 10;
        int from = Math.min(page * size, dtos.size());
        int to = Math.min(from + size, dtos.size());
        List<JobPostResponseDTO> paged = dtos.subList(from, to);

        RecommendationResponseDTO resp = new RecommendationResponseDTO();
        resp.setJobs(paged);
        resp.setPage(page);
        resp.setSize(size);
        resp.setTotal(dtos.size());
        return resp;
    }

    @GetMapping
    @Operation(
        summary = "Get job recommendations (GET)",
        description = "Universal endpoint for job recommendations via GET. Strictly follows 4 scenarios: 1) Search without filters (query, all jobs), 2) Search with filters (query + tagIds/categoryIds, only filtered jobs), 3) Recommendations for a job post (jobPostId, topK=3), 4) Recommendations for the last viewed job post (lastViewedJobPostId, topK=all). All parameters are passed as query string.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "query", description = "Search query (e.g., 'Python developer')", example = "Python developer"),
            @io.swagger.v3.oas.annotations.Parameter(name = "tagIds", description = "Tag IDs for filtering", example = "1,2,3"),
            @io.swagger.v3.oas.annotations.Parameter(name = "categoryIds", description = "Category IDs for filtering", example = "4,5"),
            @io.swagger.v3.oas.annotations.Parameter(name = "jobPostId", description = "Recommend for this job post ID", example = "123"),
            @io.swagger.v3.oas.annotations.Parameter(name = "lastViewedJobPostId", description = "Recommend for last viewed job post ID", example = "456"),
            @io.swagger.v3.oas.annotations.Parameter(name = "page", description = "Page number (default 0)", example = "0"),
            @io.swagger.v3.oas.annotations.Parameter(name = "size", description = "Page size (default 10)", example = "10")
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "List of recommended jobs",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = devtitans.antoshchuk.devfusion2025backend.dto.response.RecommendationResponseDTO.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request or parameters"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        }
    )
    public RecommendationResponseDTO recommendGet(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) List<Integer> tagIds,
            @RequestParam(required = false) List<Integer> categoryIds,
            @RequestParam(required = false) Integer jobPostId,
            @RequestParam(required = false) Integer lastViewedJobPostId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        List<Integer> allowedIds = null;
        String actualQuery = query;
        Integer topK = null;
        // Scenario 3: recommendations for a job post
        if (jobPostId != null) {
            actualQuery = recommendationService.getJobTitleById(jobPostId);
            if (actualQuery == null || actualQuery.isBlank()) {
                throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Job post not found for jobPostId=" + jobPostId);
            }
            topK = 3;
            allowedIds = new ArrayList<>();
        } else if (lastViewedJobPostId != null) {
            actualQuery = recommendationService.getJobTitleById(lastViewedJobPostId);
            if (actualQuery == null || actualQuery.isBlank()) {
                throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Job post not found for lastViewedJobPostId=" + lastViewedJobPostId);
            }
            allowedIds = new ArrayList<>();
            // topK не ограничиваем (внешняя система сама ограничит)
        } else if ((tagIds != null && !tagIds.isEmpty()) || (categoryIds != null && !categoryIds.isEmpty())) {
            allowedIds = recommendationService.getAllowedIds(categoryIds, tagIds);
            if (actualQuery == null || actualQuery.isBlank()) {
                throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Query is required for search with filters");
            }
            topK = (allowedIds != null) ? allowedIds.size() : 0;
        } else {
            allowedIds = new ArrayList<>();
            if (actualQuery == null || actualQuery.isBlank()) {
                throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Query is required");
            }
            // topK = количество всех вакансий
            topK = recommendationService.getAllJobPostsCount();
        }
        List<Integer> recommendedIds = recommendationService.getRecommendedIds(actualQuery, topK, null, allowedIds);
        List<JobPost> posts = recommendationService.getJobPostsByIds(recommendedIds);
        List<JobPostResponseDTO> dtos = posts.stream().map(jobPostMapper::toResponseDTO).toList();
        int from = Math.min(page * size, dtos.size());
        int to = Math.min(from + size, dtos.size());
        List<JobPostResponseDTO> paged = dtos.subList(from, to);
        RecommendationResponseDTO resp = new RecommendationResponseDTO();
        resp.setJobs(paged);
        resp.setPage(page);
        resp.setSize(size);
        resp.setTotal(dtos.size());
        return resp;
    }
} 