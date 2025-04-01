package devtitans.antoshchuk.devfusion2025backend.dto.request;

import devtitans.antoshchuk.devfusion2025backend.models.user.Gender;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import jakarta.persistence.Column;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserSeekerRegisterRequestDTO{
//    private UserRegisterRequestDTO userRegisterRequestDTO;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;

}
