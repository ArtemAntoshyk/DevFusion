package devtitans.antoshchuk.devfusion2025backend.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserLoginRequestDTO {
    private String email;
    private String password;
}
