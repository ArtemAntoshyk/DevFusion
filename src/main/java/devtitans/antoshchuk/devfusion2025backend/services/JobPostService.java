package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.dto.request.JobPostFilterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostDetailedResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.exceptions.ResourceNotFoundException;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import devtitans.antoshchuk.devfusion2025backend.repositories.JobPostRepository;
import devtitans.antoshchuk.devfusion2025backend.specifications.JobPostSpecification;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.JobPostMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobPostService {
    private final JobPostRepository jobPostRepository;
    private final JobPostMapper jobPostMapper;

    @Autowired
    public JobPostService(JobPostRepository jobPostRepository, JobPostMapper jobPostMapper) {
        this.jobPostRepository = jobPostRepository;
        this.jobPostMapper = jobPostMapper;
    }

    public List<JobPost> getJobPosts() {
        return jobPostRepository.findAll();
    }

    public List<JobPost> getJobPostsByCompanyId(int companyId) {
        return jobPostRepository.findJobPostsByCompanyId(companyId);
    }

    public JobPost getJobPostById(int id) {
        return jobPostRepository.findById(id).orElse(null);
    }

    public Page<JobPost> getAllJobPosts(int page, int size) {
        return jobPostRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional
    public Page<JobPostResponseDTO> getFilteredJobPosts(JobPostFilterRequestDTO filterRequest) {
        Sort sort = Sort.by(
            filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? 
            Sort.Direction.ASC : Sort.Direction.DESC,
            filterRequest.getSortBy()
        );

        PageRequest pageRequest = PageRequest.of(
            filterRequest.getPage(),
            filterRequest.getSize(),
            sort
        );

        Page<JobPost> jobPosts = jobPostRepository.findAll(
            JobPostSpecification.withFilters(
                filterRequest.getSearchQuery(),
                filterRequest.getLocation(),
                filterRequest.getCompanyId(),
                filterRequest.getJobType(),
                filterRequest.getGradation(),
                filterRequest.getIsActive()
            ),
            pageRequest
        );

        return jobPosts.map(jobPostMapper::toResponseDTO);
    }

    @Transactional
    public JobPostDetailedResponseDTO getJobPostDetails(Integer id) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Вакансію не знайдено"));
        
        return jobPostMapper.toDetailedDTO(jobPost);
    }

    @Transactional
    public JobPost save(JobPost jobPost) {
        return jobPostRepository.save(jobPost);
    }
}
