package devtitans.antoshchuk.devfusion2025backend.repositories;

import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Integer>, JpaSpecificationExecutor<JobPost> {
    List<JobPost> findTop3ByCompanyOrderByCreatedDateTimeDesc(Company company);
    
    @Query("SELECT j FROM JobPost j WHERE j.company.id = :companyId")
    List<JobPost> findJobPostsByCompanyId(@Param("companyId") int companyId);
} 