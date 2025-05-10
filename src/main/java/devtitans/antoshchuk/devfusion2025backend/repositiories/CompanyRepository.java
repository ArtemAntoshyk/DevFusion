package devtitans.antoshchuk.devfusion2025backend.repositiories;

import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface CompanyRepository extends JpaRepository<Company, Integer>, JpaSpecificationExecutor<Company> {

    Boolean existsByName(String name);
    Company save(Company newCompany);
}
