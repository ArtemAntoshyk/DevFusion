package devtitans.antoshchuk.devfusion2025backend.repositiories;


import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    UserAccount findByUsername(String username);
    UserAccount findByEmail(String email);
    boolean existsByEmailOrUsernameOrContactNumber(String email, String username, String contactNumber);
    boolean existsByEmailOrUsername(String email, String username);

}
