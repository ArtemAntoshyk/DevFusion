package devtitans.antoshchuk.devfusion2025backend.dto.response;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyJobPostShortDTO {
    private Integer id;
    private String title;
    private Date createdAt;
    private String description;
    private int applicantsCount;
} 