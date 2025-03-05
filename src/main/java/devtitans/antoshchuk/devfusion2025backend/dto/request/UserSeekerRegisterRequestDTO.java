package devtitans.antoshchuk.devfusion2025backend.dto.request;

import devtitans.antoshchuk.devfusion2025backend.models.user.Gender;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserSeekerRegisterRequestDTO extends UserRegisterRequestDTO{
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
}
