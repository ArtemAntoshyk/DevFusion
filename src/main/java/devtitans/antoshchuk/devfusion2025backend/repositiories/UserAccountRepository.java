package devtitans.antoshchuk.devfusion2025backend.repositiories;


import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    UserAccount findByEmail(String email);
    boolean existsByEmailOrContactNumber(String email, String contactNumber);
    boolean existsByEmail(String email);
}
