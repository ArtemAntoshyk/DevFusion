package devtitans.antoshchuk.devfusion2025backend.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserAccountDTO {
    private String email;
    private String contactNumber;
    private String password;
}
