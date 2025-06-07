package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.dto.request.JobPostFilterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostDetailedResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.exceptions.ResourceNotFoundException;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import devtitans.antoshchuk.devfusion2025backend.repositories.JobPostRepository;
import devtitans.antoshchuk.devfusion2025backend.specifications.JobPostSpecification;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.JobPostMapper;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyJobPostShortDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostApplicantDTO;
import devtitans.antoshchuk.devfusion2025backend.repositories.JobPostActivityRepository;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPostActivity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import devtitans.antoshchuk.devfusion2025backend.dto.response.SeekerJobPostResponseDTO;

import java.util.Date;
import java.util.List;

@Service
public class JobPostService {
    private final JobPostRepository jobPostRepository;
    private final JobPostMapper jobPostMapper;
    private final JobPostActivityRepository jobPostActivityRepository;

    @Autowired
    public JobPostService(JobPostRepository jobPostRepository, JobPostMapper jobPostMapper, JobPostActivityRepository jobPostActivityRepository) {
        this.jobPostRepository = jobPostRepository;
        this.jobPostMapper = jobPostMapper;
        this.jobPostActivityRepository = jobPostActivityRepository;
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
                filterRequest.getJobType(),
                filterRequest.getExperience(),
                filterRequest.getSkillIds()
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

    public Page<JobPost> getJobPostsByCompanyId(int companyId, int page, int size) {
        return jobPostRepository.findAll(
            (root, query, cb) -> cb.equal(root.get("company").get("id"), companyId),
            PageRequest.of(page, size)
        );
    }

    public JobPostMapper getJobPostMapper() {
        return jobPostMapper;
    }

    public Page<CompanyJobPostShortDTO> getCompanyJobPosts(int companyId, int page, int size) {
        Page<JobPost> jobPosts = jobPostRepository.findAll(
            (root, query, cb) -> cb.equal(root.get("company").get("id"), companyId),
            PageRequest.of(page, size)
        );
        return jobPosts.map(jp -> CompanyJobPostShortDTO.builder()
                .id(jp.getId())
                .title(jp.getTitle())
                .createdAt(jp.getCreatedDateTime())
                .description(jp.getJobDescription())
                .applicantsCount(jobPostActivityRepository.countByJobPostId(jp.getId()))
                .build()
        );
    }

    public JobPostDetailedResponseDTO getCompanyJobPostDetails(int companyId, int jobPostId) {
        JobPost jobPost = jobPostRepository.findById(jobPostId).orElse(null);
        if (jobPost == null || jobPost.getCompany() == null || !jobPost.getCompany().getId().equals(companyId)) {
            return null;
        }
        return jobPostMapper.toDetailedDTO(jobPost);
    }

    public List<JobPostApplicantDTO> getApplicantsForJobPost(int companyId, int jobPostId) {
        JobPost jobPost = jobPostRepository.findById(jobPostId).orElse(null);
        if (jobPost == null || jobPost.getCompany() == null || !jobPost.getCompany().getId().equals(companyId)) {
            return java.util.Collections.emptyList();
        }
        return jobPostActivityRepository.findByJobPostId(jobPostId).stream()
                .map(activity -> {
                    var seeker = activity.getSeeker();
                    var user = seeker.getUserAccount();
                    return JobPostApplicantDTO.builder()
                            .id(seeker.getId())
                            .firstName(seeker.getFirstName())
                            .lastName(seeker.getLastName())
                            .email(user.getEmail())
                            .contactNumber(user.getContactNumber())
                            .cvUrl(seeker.getCvUrl())
                            .applyDate(activity.getApplyDate())
                            .status(activity.getStatus())
                            .build();
                })
                .toList();
    }

    /**
     * Получить вакансии, на которые откликался seeker (пагинация, сортировка по дате отклика DESC)
     */
    public Page<SeekerJobPostResponseDTO> getSeekerAppliedJobPosts(int seekerId, int page, int size) {
        Page<JobPostActivity> activities = jobPostActivityRepository.findBySeekerId(seekerId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "applyDate")));
        return activities.map(activity -> {
            var jobPost = activity.getJobPost();
            return SeekerJobPostResponseDTO.builder()
                    .jobPostId(jobPost.getId())
                    .title(jobPost.getTitle())
                    .companyName(jobPost.getCompany() != null ? jobPost.getCompany().getName() : null)
                    .status(activity.getStatus())
                    .applyDate(activity.getApplyDate())
                    .build();
        });
    }

    /**
     * Проверить, откликался ли seeker на вакансию, и получить дату отклика
     */
    public HasAppliedResponse hasSeekerAppliedToJobPost(Integer seekerId, Integer jobPostId) {
        var activity = jobPostActivityRepository.findFirstBySeekerIdAndJobPostId(seekerId, jobPostId);
        if (activity != null) {
            return new HasAppliedResponse(true, activity.getApplyDate());
        }
        return new HasAppliedResponse(false, null);
    }

    public static class HasAppliedResponse {
        public boolean hasApplied;
        public Date applyDate;
        public HasAppliedResponse(boolean hasApplied, Date applyDate) {
            this.hasApplied = hasApplied;
            this.applyDate = applyDate;
        }
        public boolean isHasApplied() { return hasApplied; }
        public Date getApplyDate() { return applyDate; }
    }
}
