package devtitans.antoshchuk.devfusion2025backend.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserAccountDTO {
    private String username;
    private String email;
    private String gender;
    private String contactNumber;
    private String password;
}
