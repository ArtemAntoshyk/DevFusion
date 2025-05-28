package devtitans.antoshchuk.devfusion2025backend.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobPostUpdateRequestDTO {
    private String title;
    private String description;
    private List<String> requirements;
    private List<String> responsibilities;
    private String salaryRange;
    private String location;
    private String employmentType;
    private String experienceLevel;
} 