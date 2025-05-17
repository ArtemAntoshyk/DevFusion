package devtitans.antoshchuk.devfusion2025backend.repositiories;

import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.List;

@EnableJpaRepositories
public interface CompanyRepository extends JpaRepository<Company, Integer>, JpaSpecificationExecutor<Company> {

    Boolean existsByName(String name);
    Company save(Company newCompany);

    @Query("SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.user WHERE 1=1")
    List<Company> findAllCompaniesBasic();

    @Query(value = "SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.user " +
           "WHERE (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:businessStream IS NULL OR LOWER(c.businessStreamName) = LOWER(:businessStream))")
    Page<Company> findFilteredCompanies(@Param("search") String search, 
                                      @Param("businessStream") String businessStream,
                                      Pageable pageable);
}
