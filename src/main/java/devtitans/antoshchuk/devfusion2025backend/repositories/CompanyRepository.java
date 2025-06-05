package devtitans.antoshchuk.devfusion2025backend.repositories;

import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer>, JpaSpecificationExecutor<Company> {
    
    Boolean existsByName(String name);
    
    Company save(Company newCompany);

    @Query("SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.user WHERE 1=1")
    List<Company> findAllCompaniesBasic();

    @Query("SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.user u WHERE u.id = :userId")
    Company findByUserId(@Param("userId") int userId);

    @Query(value = "SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.user " +
           "WHERE (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:businessStream IS NULL OR LOWER(c.businessStreamName) = LOWER(:businessStream))")
    Page<Company> findFilteredCompanies(@Param("search") String search, 
                                      @Param("businessStream") String businessStream,
                                      Pageable pageable);

    @Query(value = """
           SELECT DISTINCT c, COUNT(jp) as jobCount 
           FROM Company c 
           LEFT JOIN c.jobPosts jp 
           LEFT JOIN FETCH c.user u 
           GROUP BY c.id, c.name, c.logo, c.businessStreamName, c.companyDescription, 
                    u.id, u.email, u.password, u.contactNumber, u.userImage, u.active, 
                    u.emailNotificationActive, u.registrationDate 
           ORDER BY COUNT(jp) DESC
           """)
    List<Object[]> findTopCompaniesByVacancyCount(@Param("limit") int limit);

    @Query("SELECT DISTINCT c FROM Company c " +
           "LEFT JOIN FETCH c.jobPosts jp " +
           "LEFT JOIN FETCH c.user u " +
           "ORDER BY c.id")
    List<Company> findAll();
} 