package devtitans.antoshchuk.devfusion2025backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JobPostBaseResponseDTO {
    private int id;
    private CompanyBaseResponseDTO company;
    private JobTypeBaseResponseDTO type;
    private JobGradationBaseResponseDTO gradation;
//    private String jobType;
//    private String jobGradation;
    private String title;
    private String jobDescription;
    private String jobLocation;
    private double latitude;
    private double longitude;
    private boolean isCompanyNameHidden;
    private Date createdAt;
    private boolean isActive;
}