package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.dto.request.RecommendationRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import devtitans.antoshchuk.devfusion2025backend.repositories.JobPostRepository;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.JobPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final JobPostRepository jobPostRepository;
    private final JobPostMapper jobPostMapper;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String RECOMMEND_URL = "https://b7a2-2a01-cb01-3033-7f6b-3519-4c7c-c161-c360.ngrok-free.app/recommend";

    public List<Integer> getRecommendedIds(String query, Integer topK, Double threshold, List<Integer> allowedIds) {
        Map<String, Object> body = new HashMap<>();
        body.put("query", query != null ? query : "");
        if (topK != null) body.put("top_k", topK);
        if (threshold != null) body.put("threshold", threshold);
        if (allowedIds != null) body.put("allowed_ids", allowedIds);
        else body.put("allowed_ids", new ArrayList<>());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<List> response = restTemplate.postForEntity(RECOMMEND_URL, request, List.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return ((List<?>) response.getBody()).stream().map(Object::toString).map(Integer::parseInt).toList();
        }
        return Collections.emptyList();
    }

    public List<JobPost> getJobPostsByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        System.out.println("ids: " + ids);
        List<JobPost> posts = jobPostRepository.findAllWithDetailsByIdIn(ids);
        System.out.println("executed");
        // Сохраняем порядок
        Map<Integer, JobPost> postMap = posts.stream().collect(Collectors.toMap(JobPost::getId, jp -> jp));
        return ids.stream().map(postMap::get).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<Integer> getAllowedIds(List<Integer> categoryIds, List<Integer> tagIds) {
        if (tagIds != null && !tagIds.isEmpty()) {
            return jobPostRepository.findIdsByTagIds(tagIds);
        }
        // TODO: добавить фильтрацию по категориям, если появятся
        return null;
    }

    public String getJobTitleById(Integer jobPostId) {
        if (jobPostId == null) return null;
        return jobPostRepository.findById(jobPostId).map(JobPost::getTitle).orElse(null);
    }

    public int getAllJobPostsCount() {
        return (int) jobPostRepository.count();
    }

    public JobPostRepository getJobPostRepository() {
        return jobPostRepository;
    }
} 