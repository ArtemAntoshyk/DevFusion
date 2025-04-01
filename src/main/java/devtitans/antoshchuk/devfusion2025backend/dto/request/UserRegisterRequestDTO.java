package devtitans.antoshchuk.devfusion2025backend.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRegisterRequestDTO {
    private UserAccountDTO user;
    private UserSeekerRegisterRequestDTO seeker;
    private UserCompanyRegisterRequestDTO company;

    @Override
    public String toString() {
        return "UserRegisterRequestDTO{" +
                "user=" + user +
                ", seeker=" + seeker +
                ", company=" + company +
                '}';
    }
//    @Override
//    public String toString() {
//        return "UserRegisterRequestDTO{" +
//                "username='" + username + '\'' +
//                ", email='" + email + '\'' +
//                ", gender='" + gender + '\'' +
//                ", contactNumber='" + contactNumber + '\'' +
//                ", password='" + password + '\'' +
//                '}';
//    }
}
