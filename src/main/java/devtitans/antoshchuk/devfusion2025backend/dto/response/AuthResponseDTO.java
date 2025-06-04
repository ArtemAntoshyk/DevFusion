package devtitans.antoshchuk.devfusion2025backend.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String role;
    private Long userId;
} 