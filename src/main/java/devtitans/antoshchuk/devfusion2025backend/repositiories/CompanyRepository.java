package devtitans.antoshchuk.devfusion2025backend.repositiories;

import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    public Company findById(int id);
}
