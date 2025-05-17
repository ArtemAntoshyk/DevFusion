package devtitans.antoshchuk.devfusion2025backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Schema(description = "Detailed information about a job post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobPostDetailedResponseDTO {
    @Schema(description = "Job post ID", example = "1")
    private Integer id;

    @Schema(description = "Job title in default language", example = "Senior Java Developer")
    private String title;

    @Schema(description = "Job title in English", example = "Senior Java Developer")
    private String titleEn;

    @Schema(description = "Job location", example = "Kyiv, Ukraine")
    private String jobLocation;

    @Schema(description = "Detailed job description in default language")
    private String jobDescription;

    @Schema(description = "Detailed job description in English")
    private String jobDescriptionEn;

    @Schema(description = "Whether company name is hidden", example = "false")
    private boolean isCompanyNameHidden;

    @Schema(description = "Job type information")
    private JobTypeDTO jobType;

    @Schema(description = "Job gradation information")
    private JobGradationDTO jobGradation;

    @Schema(description = "Creation date and time", example = "2024-03-15T14:30:00")
    private Date createdDateTime;

    @Schema(description = "Whether the job post is active", example = "true")
    private boolean isActive;

    @Schema(description = "Salary information", example = "5000-7000 USD")
    private String salary;

    @Schema(description = "Primary language requirement", example = "Ukrainian")
    private String language;

    @Schema(description = "Detailed company information")
    private CompanyDetailsDTO company;
} 