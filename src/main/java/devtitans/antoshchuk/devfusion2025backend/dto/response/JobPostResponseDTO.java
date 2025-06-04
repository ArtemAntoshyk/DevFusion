package devtitans.antoshchuk.devfusion2025backend.dto.response;

import devtitans.antoshchuk.devfusion2025backend.dto.JobPostSkillDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.RequiredExperienceDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.TagDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.Set;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Basic job post information")
public class JobPostResponseDTO {
    @Schema(description = "Job post ID", example = "1")
    private Integer id;

    @NotBlank
    @Schema(description = "Job title", example = "Senior Java Developer")
    private String title;

    @NotBlank
    @Schema(description = "Job title in English", example = "Senior Java Developer")
    private String titleEn;

    @NotBlank
    @Schema(description = "Job description", example = "We are looking for an experienced Java developer...")
    private String description;

    @NotBlank
    @Schema(description = "Job description in English", example = "We are looking for an experienced Java developer...")
    private String descriptionEn;

    @NotBlank
    @Schema(description = "Location", example = "London, UK")
    private String location;

    @NotBlank
    @Schema(description = "Salary range", example = "$80,000 - $120,000")
    private String salaryRange;

    @NotBlank
    @Schema(description = "Employment type", example = "Full-time")
    private String employmentType;

    @NotBlank
    @Schema(description = "Experience level", example = "Senior")
    private String experienceLevel;

    @NotNull
    @Schema(description = "Required experience")
    private RequiredExperienceDTO requiredExperience;

    @NotEmpty
    @Schema(description = "Job tags")
    private Set<TagDTO> tags;

    @NotEmpty
    @Schema(description = "Required skills")
    private Set<JobPostSkillDTO> skills;

    @NotBlank
    @Schema(description = "Language", example = "English")
    private String language;

    @NotNull
    @Schema(description = "Company information")
    private CompanyDTO company;

    @NotNull
    @Schema(description = "Creation date")
    private Date createdAt;

    @Schema(description = "Is job post active")
    private boolean isActive;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Company information")
    public static class CompanyDTO {
        @Schema(description = "Company ID", example = "1")
        private Integer id;

        @Schema(description = "Company name", example = "Tech Solutions Inc.")
        private String name;

        @Schema(description = "Company logo URL", example = "https://example.com/logo.png")
        private String logo;
    }
} 