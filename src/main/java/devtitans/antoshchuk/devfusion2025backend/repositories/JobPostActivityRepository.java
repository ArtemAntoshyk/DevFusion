package devtitans.antoshchuk.devfusion2025backend.repositories;

import devtitans.antoshchuk.devfusion2025backend.models.job.JobPostActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostActivityRepository extends JpaRepository<JobPostActivity, Integer> {
    int countByJobPostId(Integer jobPostId);
    java.util.List<devtitans.antoshchuk.devfusion2025backend.models.job.JobPostActivity> findByJobPostId(Integer jobPostId);
    Page<JobPostActivity> findBySeekerId(Integer seekerId, Pageable pageable);
    JobPostActivity findFirstBySeekerIdAndJobPostId(Integer seekerId, Integer jobPostId);
} 