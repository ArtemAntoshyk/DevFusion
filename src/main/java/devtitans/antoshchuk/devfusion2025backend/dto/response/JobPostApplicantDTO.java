package devtitans.antoshchuk.devfusion2025backend.dto.response;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostApplicantDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String cvUrl;
    private Date applyDate;
    private String status;
} 