package devtitans.antoshchuk.devfusion2025backend.repositories;

import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Integer>, JpaSpecificationExecutor<JobPost> {
    List<JobPost> findTop3ByCompanyOrderByCreatedDateTimeDesc(Company company);
    
    @Query("SELECT j FROM JobPost j WHERE j.company.id = :companyId")
    List<JobPost> findJobPostsByCompanyId(@Param("companyId") int companyId);

    @Query("SELECT DISTINCT j.id FROM JobPost j JOIN j.tags t WHERE (:tagIds IS NULL OR t.id IN :tagIds)")
    List<Integer> findIdsByTagIds(@Param("tagIds") List<Integer> tagIds);

    @EntityGraph(attributePaths = {"company", "jobType", "jobGradation", "experience", "tags", "jobPostSkillSets", "jobPostSkillSets.skill"})
    List<JobPost> findAllWithDetailsByIdIn(List<Integer> ids);

    @Query("""
        SELECT DISTINCT jp.id
        FROM JobPost jp
        JOIN jp.tags t
        WHERE (:jobTypeIds IS NULL OR jp.jobType.id IN :jobTypeIds)
          AND (:gradationIds IS NULL OR jp.jobGradation.id IN :gradationIds)
          AND (:tagIds IS NULL OR t.id IN :tagIds)
    """)
    List<Integer> findIdsByJobTypeAndGradationAndTags(
        @Param("jobTypeIds") List<Integer> jobTypeIds,
        @Param("gradationIds") List<Integer> gradationIds,
        @Param("tagIds") List<Integer> tagIds
    );
} 