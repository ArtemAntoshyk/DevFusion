package devtitans.antoshchuk.devfusion2025backend.dto.request;

import devtitans.antoshchuk.devfusion2025backend.dto.JobPostSkillDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobPostCreateRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "English title is required")
    private String titleEn;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "English description is required")
    private String descriptionEn;

    @NotEmpty(message = "At least one requirement is required")
    private List<String> requirements;

    @NotEmpty(message = "At least one responsibility is required")
    private List<String> responsibilities;

    @NotBlank(message = "Salary range is required")
    private String salaryRange;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Employment type is required")
    private String employmentType;

    @NotBlank(message = "Experience level is required")
    private String experienceLevel;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    @NotNull(message = "Required experience ID is required")
    private Integer requiredExperienceId;

    @NotEmpty(message = "At least one tag is required")
    private Set<Integer> tagIds;

    @NotEmpty(message = "At least one skill is required")
    private Set<JobPostSkillDTO> skills;

    @NotBlank(message = "Language is required")
    private String language;
} 