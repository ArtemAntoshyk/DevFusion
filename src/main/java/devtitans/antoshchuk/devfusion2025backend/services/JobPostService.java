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
import org.springframework.data.jpa.domain.Specification;
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

    public JobPost getJobPostById(int id) {
        return jobPostRepository.findById(id).orElse(null);
    }

    public Page<JobPost> getAllJobPosts(int page, int size) {
        return jobPostRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional
    public Page<JobPostResponseDTO> getFilteredJobPosts(JobPostFilterRequestDTO filterRequest, String active) {
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
                filterRequest.getSkillIds(),
                active
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

    public Page<CompanyJobPostShortDTO> getCompanyJobPosts(int companyId, int page, int size, String active) {
        Specification<JobPost> spec = (root, query, cb) -> cb.equal(root.get("company").get("id"), companyId);
        if (active != null && !"all".equalsIgnoreCase(active)) {
            boolean isActive = !"false".equalsIgnoreCase(active);
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isActive"), isActive));
        }
        Page<JobPost> jobPosts = jobPostRepository.findAll(
            spec,
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
                            .activityId(activity.getId())
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
                    .activityId(activity.getId())
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

    /**
     * Update the status of a job application (JobPostActivity) by activity ID and company ID.
     * @param activityId the ID of the job application activity
     * @param newStatus the new status to set
     * @param companyId the ID of the company (for ownership check)
     * @return true if updated, false if not found or not owned by the company
     */
    public boolean updateJobPostActivityStatus(Integer activityId, String newStatus, Integer companyId) {
        JobPostActivity activity = jobPostActivityRepository.findById(activityId).orElse(null);
        if (activity == null) return false;
        JobPost jobPost = activity.getJobPost();
        if (jobPost == null || jobPost.getCompany() == null || !jobPost.getCompany().getId().equals(companyId)) {
            return false;
        }
        activity.setStatus(newStatus);
        activity.setDecisionAt(java.time.Instant.now());
        jobPostActivityRepository.save(activity);
        return true;
    }

    /**
     * Soft delete a job post by id and company id. Only the owner company can delete.
     * @param jobPostId the id of the job post
     * @param companyId the id of the company
     * @return true if updated, false if not found or not owned by the company
     */
    public boolean deleteJobPostByCompany(Integer jobPostId, Integer companyId) {
        JobPost jobPost = jobPostRepository.findById(jobPostId).orElse(null);
        if (jobPost == null || jobPost.getCompany() == null || !jobPost.getCompany().getId().equals(companyId)) {
            return false;
        }
        jobPost.setActive(false);
        jobPostRepository.save(jobPost);
        return true;
    }

    /**
     * Hard delete a job post by id and company id. Only the owner company can delete.
     * @param jobPostId the id of the job post
     * @param companyId the id of the company
     * @return true if deleted, false if not found or not owned by the company
     */
    public boolean hardDeleteJobPostByCompany(Integer jobPostId, Integer companyId) {
        JobPost jobPost = jobPostRepository.findById(jobPostId).orElse(null);
        if (jobPost == null || jobPost.getCompany() == null || !jobPost.getCompany().getId().equals(companyId)) {
            return false;
        }
        jobPostRepository.delete(jobPost);
        return true;
    }

    public List<JobPost> getJobPosts(String active) {
        Specification<JobPost> spec = (root, query, cb) -> cb.conjunction();
        if (active != null && !"all".equalsIgnoreCase(active)) {
            boolean isActive = !"false".equalsIgnoreCase(active);
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isActive"), isActive));
        }
        return jobPostRepository.findAll(spec);
    }

    public List<JobPost> getJobPostsByCompanyId(int companyId, String active) {
        Specification<JobPost> spec = (root, query, cb) -> cb.equal(root.get("company").get("id"), companyId);
        if (active != null && !"all".equalsIgnoreCase(active)) {
            boolean isActive = !"false".equalsIgnoreCase(active);
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isActive"), isActive));
        }
        return jobPostRepository.findAll(spec);
    }

    public Page<JobPost> getJobPostsByCompanyId(int companyId, int page, int size, String active) {
        Specification<JobPost> spec = (root, query, cb) -> cb.equal(root.get("company").get("id"), companyId);
        if (active != null && !"all".equalsIgnoreCase(active)) {
            boolean isActive = !"false".equalsIgnoreCase(active);
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isActive"), isActive));
        }
        return jobPostRepository.findAll(spec, PageRequest.of(page, size));
    }

    @Deprecated
    public Page<JobPost> getJobPostsByCompanyId(int companyId, int page, int size) {
        return getJobPostsByCompanyId(companyId, page, size, "true");
    }

    public JobPostMapper getJobPostMapper() {
        return jobPostMapper;
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

    public long countAllJobPosts() {
        return jobPostRepository.count();
    }

    public Page<JobPost> findJobPostsByIds(List<Integer> ids, int page, int size) {
        List<JobPost> all = jobPostRepository.findAllById(ids);
        int start = Math.min(page * size, all.size());
        int end = Math.min(start + size, all.size());
        List<JobPost> sublist = all.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(sublist, PageRequest.of(page, size), all.size());
    }
}
