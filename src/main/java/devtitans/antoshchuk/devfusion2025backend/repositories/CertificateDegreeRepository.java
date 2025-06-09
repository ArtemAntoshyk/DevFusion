package devtitans.antoshchuk.devfusion2025backend.repositories;

import devtitans.antoshchuk.devfusion2025backend.models.user.CertificateDegree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateDegreeRepository extends JpaRepository<CertificateDegree, Integer> {
} 