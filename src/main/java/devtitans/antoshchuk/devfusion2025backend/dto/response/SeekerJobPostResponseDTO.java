package devtitans.antoshchuk.devfusion2025backend.dto.response;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeekerJobPostResponseDTO {
    private Integer jobPostId;
    private String title;
    private String companyName;
    private String status;
    private Date applyDate;
} 