package devtitans.antoshchuk.devfusion2025backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDataResponseDTO {
    private String email;
    private String fullName;
    private String userType;
    private String contactNumber;
    // Add other fields as needed
}