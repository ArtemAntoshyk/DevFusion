package devtitans.antoshchuk.devfusion2025backend.dto.response;

import devtitans.antoshchuk.devfusion2025backend.dto.JobPostSkillDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.RequiredExperienceDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.TagDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed job post information")
public class JobPostDetailedResponseDTO {
    @Schema(description = "Job post ID", example = "1")
    private Integer id;

    @Schema(description = "Job title", example = "Senior Java Developer")
    private String title;

    @Schema(description = "Job title in English", example = "Senior Java Developer")
    private String titleEn;

    @Schema(description = "Job location", example = "London, UK")
    private String jobLocation;

    @Schema(description = "Detailed job description", example = "We are looking for an experienced Java developer...")
    private String jobDescription;

    @Schema(description = "Job description in English")
    private String jobDescriptionEn;

    @Schema(description = "Whether company name is hidden", example = "false")
    private boolean isCompanyNameHidden;

    @Schema(description = "Job type")
    private JobTypeDTO jobType;

    @Schema(description = "Experience level")
    private JobGradationDTO jobGradation;

    @Schema(description = "Creation date", example = "2024-03-15T14:30:00")
    private Date createdDateTime;

    @Schema(description = "Whether the job post is active", example = "true")
    private boolean isActive;

    @Schema(description = "Salary range", example = "5000-7000 USD")
    private String salary;

    @Schema(description = "Primary communication language", example = "English")
    private String language;

    @Schema(description = "Detailed company information")
    private CompanyDetailsDTO company;

    @Schema(description = "Required experience")
    private RequiredExperienceDTO requiredExperience;

    @Schema(description = "Tags")
    private Set<TagDTO> tags;

    @Schema(description = "Skills")
    private Set<JobPostSkillDTO> skills;
} 