package devtitans.antoshchuk.devfusion2025backend.dto.response;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HasAppliedResponseDTO {
    private boolean hasApplied;
    private Date applyDate;
} 