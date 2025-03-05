package devtitans.antoshchuk.devfusion2025backend.repositiories;

import devtitans.antoshchuk.devfusion2025backend.models.user.Seeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface SeekerRepository extends JpaRepository<Seeker,Integer> {
}
