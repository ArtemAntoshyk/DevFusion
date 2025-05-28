package devtitans.antoshchuk.devfusion2025backend.repositories;

import devtitans.antoshchuk.devfusion2025backend.models.user.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Integer> {
    UserType findByName(String name);
} 