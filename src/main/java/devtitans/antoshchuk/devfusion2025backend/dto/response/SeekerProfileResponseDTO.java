package devtitans.antoshchuk.devfusion2025backend.dto.response;

import devtitans.antoshchuk.devfusion2025backend.models.user.EducationDetail;
import devtitans.antoshchuk.devfusion2025backend.models.user.ExperienceDetail;
import devtitans.antoshchuk.devfusion2025backend.models.user.SeekerSkillSet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Seeker profile response data")
public class SeekerProfileResponseDTO {

    @Schema(description = "Seeker ID", example = "1")
    private Integer id;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Schema(description = "Date of birth", example = "1990-01-01")
    private Date dateOfBirth;

    @Schema(description = "Current monthly salary", example = "5000.0")
    private Double currentMonthlySalary;

    @Schema(description = "CV URL", example = "https://example.com/cv.pdf")
    private String cvUrl;

    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Contact number", example = "+380501234567")
    private String contactNumber;

    @Schema(description = "List of skills")
    private List<SeekerSkillSetDTO> skills;

    @Schema(description = "List of education details")
    private List<EducationDetailDTO> education;

    @Schema(description = "List of experience details")
    private List<ExperienceDetailDTO> experience;

    @Schema(description = "Registration date", example = "2024-01-01T00:00:00")
    private Date registrationDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeekerSkillSetDTO {
        private Integer skillId;
        private Short skillLevel;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EducationDetailDTO {
        private Integer certificateDegreeId;
        private String major;
        private String instituteOrUniversityName;
        private String startDate;
        private String completionDate;
        private Integer cgpa;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperienceDetailDTO {
        private Boolean isCurrentJob;
        private String startDate;
        private String endDate;
        private String jobTitle;
        private String companyName;
        private String jobLocationCity;
        private String jobLocationCountry;
        private String description;
    }
} 