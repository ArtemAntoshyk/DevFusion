package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import devtitans.antoshchuk.devfusion2025backend.repositiories.JobPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobPostService {
    private JobPostRepository jobPostRepository;

    @Autowired
    public JobPostService(JobPostRepository jobPostRepository) {
        this.jobPostRepository = jobPostRepository;
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
}
