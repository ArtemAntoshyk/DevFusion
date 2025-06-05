package devtitans.antoshchuk.devfusion2025backend.dto.request;

import devtitans.antoshchuk.devfusion2025backend.dto.JobPostSkillDTO;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request for creating a new job post")
public class JobPostCreateRequestDTO {
    @NotBlank(message = "Title is required")
    @Schema(description = "Job title", example = "Senior Java Developer")
    private String title;

    @NotBlank(message = "English title is required")
    @Schema(description = "Job title in English", example = "Senior Java Developer")
    private String titleEn;

    @NotBlank(message = "Description is required")
    @Schema(description = "Job description", example = "We are looking for an experienced Java developer...")
    private String description;

    @NotBlank(message = "English description is required")
    @Schema(description = "Job description in English", example = "We are looking for an experienced Java developer...")
    private String descriptionEn;

    @NotEmpty(message = "At least one requirement is required")
    @Schema(description = "List of requirements", example = "['5+ years of Java experience', 'Spring Framework knowledge']")
    private List<String> requirements;

    @NotEmpty(message = "At least one responsibility is required")
    @Schema(description = "List of responsibilities", example = "['Develop backend services', 'Participate in code reviews']")
    private List<String> responsibilities;

    @NotBlank(message = "Salary range is required")
    @Schema(description = "Salary range", example = "$80,000 - $120,000")
    private String salaryRange;

    @NotBlank(message = "Location is required")
    @Schema(description = "Job location", example = "London, UK")
    private String location;

    @NotNull(message = "Job type ID is required")
    @Schema(description = "Job type ID", example = "1")
    private Integer jobTypeId;

    @NotNull(message = "Job gradation ID is required")
    @Schema(description = "Job gradation ID", example = "2")
    private Integer jobGradationId;

    @NotNull(message = "Required experience ID is required")
    @Schema(description = "Required experience ID", example = "3")
    private Integer requiredExperienceId;

    @NotEmpty(message = "At least one tag is required")
    @Schema(description = "List of tag IDs", example = "[1, 2, 3]")
    private Set<Integer> tagIds;

    @NotEmpty(message = "At least one skill is required")
    @Schema(description = "List of skills with level", example = "[{\"id\":1,\"level\":3},{\"id\":2,\"level\":2}]")
    private Set<JobPostSkillDTO> skills;

    @NotBlank(message = "Language is required")
    @Schema(description = "Primary communication language", example = "English")
    private String language;
} 