package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.request.RecommendationRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.RecommendationResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import devtitans.antoshchuk.devfusion2025backend.services.JobPostService;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.JobPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final JobPostService jobPostService;
    private final JobPostMapper jobPostMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${recommendation.service.url:https://094a-2a01-cb01-3001-96a9-b140-d63-5c19-7360.ngrok-free.app/recommend}")
    private String recommendationServiceUrl;

    @Operation(
        summary = "Get job recommendations by search query",
        description = "Returns a paginated list of recommended jobs based on the search query. Uses the external recommendation system for ranking.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(example = "{\"query\": \"Java developer\"}"),
                examples = @ExampleObject(value = "{\n  \"query\": \"Java developer\"\n}")
            )
        ),
        parameters = {
            @Parameter(name = "page", description = "Page number (starts from 0)", example = "0"),
            @Parameter(name = "size", description = "Page size", example = "6")
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Paginated list of recommended jobs",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RecommendationResponseDTO.class),
                    examples = @ExampleObject(value = "{\n  \"jobs\": [ /* JobPostResponseDTO */ ],\n  \"page\": 0,\n  \"size\": 6,\n  \"total\": 10\n}")
                )
            )
        }
    )
    @PostMapping("/search")
    public ResponseEntity<RecommendationResponseDTO> recommendByQuery(
            @RequestBody Map<String, Object> requestBody,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        String query = (String) requestBody.getOrDefault("query", "");
        long totalJobs = jobPostService.countAllJobPosts();

        Map<String, Object> recommendRequest = Map.of(
                "query", query,
                "top_k", (int) totalJobs,
                "threshold", 0.5,
                "allowed_ids", Collections.emptyList()
        );

        List<Integer> recommendedIds = restTemplate.postForObject(recommendationServiceUrl, recommendRequest, List.class);
        if (recommendedIds == null) {
            recommendedIds = Collections.emptyList();
        }

        final List<Integer> recommendedIdsFinal = recommendedIds;
        List<JobPost> allJobPosts = jobPostService.getJobPosts().stream()
                .filter(jp -> recommendedIdsFinal.contains(jp.getId()))
                .collect(Collectors.toList());
        Map<Integer, JobPost> jobPostMap = allJobPosts.stream().collect(Collectors.toMap(JobPost::getId, jp -> jp));
        List<Integer> existingIds = recommendedIdsFinal.stream()
                .filter(jobPostMap::containsKey)
                .collect(Collectors.toList());
        List<JobPostResponseDTO> jobs = existingIds.stream()
                .skip((long) page * size)
                .limit(size)
                .map(jobPostMap::get)
                .map(jobPostMapper::toResponseDTO)
                .collect(Collectors.toList());
        RecommendationResponseDTO result = new RecommendationResponseDTO();
        result.setJobs(jobs);
        result.setPage(page);
        result.setSize(size);
        result.setTotal(existingIds.size());
        return ResponseEntity.ok(result);
    }

    @Operation(
        summary = "Get filtered job recommendations",
        description = "Returns a paginated list of recommended jobs based on search query and filters: experience, job type, tags, city, skills. Uses the external recommendation system for ranking.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(example = "{\"query\": \"Java\", \"experienceIds\": [1,2], \"jobTypeIds\": [3], \"tagIds\": [5,6], \"city\": \"Kyiv\", \"skillIds\": [10,11] }"),
                examples = @ExampleObject(value = "{\n  \"query\": \"Java\",\n  \"experienceIds\": [1,2],\n  \"jobTypeIds\": [3],\n  \"tagIds\": [5,6],\n  \"city\": \"Kyiv\",\n  \"skillIds\": [10,11]\n}")
            )
        ),
        parameters = {
            @Parameter(name = "page", description = "Page number (starts from 0)", example = "0"),
            @Parameter(name = "size", description = "Page size", example = "6")
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Paginated list of filtered recommended jobs",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RecommendationResponseDTO.class),
                    examples = @ExampleObject(value = "{\n  \"jobs\": [ /* JobPostResponseDTO */ ],\n  \"page\": 0,\n  \"size\": 6,\n  \"total\": 10\n}")
                )
            )
        }
    )
    @PostMapping("/filtered")
    public ResponseEntity<RecommendationResponseDTO> recommendFiltered(
            @RequestBody Map<String, Object> requestBody,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        String query = (String) requestBody.getOrDefault("query", "");
        List<Integer> experienceIds = (List<Integer>) requestBody.getOrDefault("experienceIds", Collections.emptyList());
        List<Integer> jobTypeIds = (List<Integer>) requestBody.getOrDefault("jobTypeIds", Collections.emptyList());
        List<Integer> tagIds = (List<Integer>) requestBody.getOrDefault("tagIds", Collections.emptyList());
        String city = (String) requestBody.getOrDefault("city", null);
        List<Integer> skillIds = (List<Integer>) requestBody.getOrDefault("skillIds", Collections.emptyList());

        // Фильтруем вакансии в БД по всем параметрам (если список пустой — не фильтруем по нему)
        List<JobPost> filtered = jobPostService.getJobPosts().stream()
                .filter(jp -> (experienceIds.isEmpty() || experienceIds.contains(jp.getExperience().getId())))
                .filter(jp -> (jobTypeIds.isEmpty() || (jp.getJobType() != null && jobTypeIds.contains(jp.getJobType().getId()))))
                .filter(jp -> (tagIds.isEmpty() || jp.getTags().stream().anyMatch(t -> tagIds.contains(t.getId()))))
                .filter(jp -> (city == null || city.isEmpty() || (jp.getJobLocation() != null && jp.getJobLocation().toLowerCase().contains(city.toLowerCase()))))
                .filter(jp -> (skillIds.isEmpty() || jp.getJobPostSkillSets().stream().anyMatch(s -> skillIds.contains(s.getSkill().getId()))))
                .toList();
        List<Integer> allowedIds = filtered.stream().map(JobPost::getId).toList();
        System.out.println(allowedIds);

        long totalJobs = jobPostService.countAllJobPosts();
        // Формируем запрос к внешнему сервису
        Map<String, Object> recommendRequest = Map.of(
                "query", query,
                "top_k", (int) totalJobs,
                "threshold", 0.5,
                "allowed_ids", allowedIds
        );
        // Получаем список id вакансий от рекомендательной системы
        List<Integer> recommendedIds = restTemplate.postForObject(recommendationServiceUrl, recommendRequest, List.class);
        System.out.println(recommendedIds);

        if (recommendedIds == null) {
            recommendedIds = Collections.emptyList();
        }
        // Получаем все вакансии по id, которые реально есть в БД
        final List<Integer> recommendedIdsFinal2 = recommendedIds;
        List<JobPost> allJobPosts = jobPostService.getJobPosts().stream()
                .filter(jp -> recommendedIdsFinal2.contains(jp.getId()))
                .collect(Collectors.toList());
        Map<Integer, JobPost> jobPostMap = allJobPosts.stream().collect(Collectors.toMap(JobPost::getId, jp -> jp));
        // Собираем id, которые реально есть в БД, в правильном порядке
        List<Integer> existingIds = recommendedIdsFinal2.stream()
                .filter(jobPostMap::containsKey)
                .collect(Collectors.toList());
        // Пагинируем по существующим id
        List<JobPostResponseDTO> jobs = existingIds.stream()
                .skip((long) page * size)
                .limit(size)
                .map(jobPostMap::get)
                .map(jobPostMapper::toResponseDTO)
                .collect(Collectors.toList());
        RecommendationResponseDTO result = new RecommendationResponseDTO();
        result.setJobs(jobs);
        result.setPage(page);
        result.setSize(size);
        result.setTotal(existingIds.size());
        return ResponseEntity.ok(result);
    }

    @Operation(
        summary = "Get recommendations for a specific job post",
        description = "Returns 3 recommended jobs based on the title of the given job post. Uses the external recommendation system for ranking.",
        parameters = {
            @Parameter(name = "jobPostId", description = "ID of the job post to get recommendations for", example = "123")
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "List of 3 recommended jobs",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RecommendationResponseDTO.class),
                    examples = @ExampleObject(value = "{\n  \"jobs\": [ /* JobPostResponseDTO */ ],\n  \"page\": 0,\n  \"size\": 3,\n  \"total\": 3\n}")
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Job post not found"
            )
        }
    )
    @GetMapping("/for-job/{jobPostId}")
    public ResponseEntity<RecommendationResponseDTO> recommendForJob(
            @PathVariable Integer jobPostId
    ) {
        JobPost jobPost = jobPostService.getJobPostById(jobPostId);
        if (jobPost == null) {
            return ResponseEntity.notFound().build();
        }
        String query = jobPost.getTitle();
        Map<String, Object> recommendRequest = Map.of(
                "query", query,
                "top_k", 3,
                "threshold", 0.5,
                "allowed_ids", Collections.emptyList()
        );
        List<Integer> recommendedIds = restTemplate.postForObject(recommendationServiceUrl, recommendRequest, List.class);
        if (recommendedIds == null) {
            recommendedIds = Collections.emptyList();
        }
        // Получаем все вакансии по id, которые реально есть в БД
        final List<Integer> recommendedIdsFinal3 = recommendedIds;
        List<JobPost> allJobPosts = jobPostService.getJobPosts().stream()
                .filter(jp -> recommendedIdsFinal3.contains(jp.getId()))
                .collect(Collectors.toList());
        Map<Integer, JobPost> jobPostMap = allJobPosts.stream().collect(Collectors.toMap(JobPost::getId, jp -> jp));
        // Собираем id, которые реально есть в БД, в правильном порядке
        List<Integer> existingIds = recommendedIdsFinal3.stream()
                .filter(jobPostMap::containsKey)
                .skip(1)
                .limit(3)
                .collect(Collectors.toList());
        List<JobPostResponseDTO> jobs = existingIds.stream()
                .map(jobPostMap::get)
                .map(jobPostMapper::toResponseDTO)
                .collect(Collectors.toList());
        RecommendationResponseDTO result = new RecommendationResponseDTO();
        result.setJobs(jobs);
        result.setPage(0);
        result.setSize(3);
        result.setTotal(existingIds.size());
        return ResponseEntity.ok(result);
    }
} 