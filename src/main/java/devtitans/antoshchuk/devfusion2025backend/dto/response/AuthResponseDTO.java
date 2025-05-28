package devtitans.antoshchuk.devfusion2025backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String role;
    private Long userId;
} 