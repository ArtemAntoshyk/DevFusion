package devtitans.antoshchuk.devfusion2025backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Schema(description = "Seeker profile update request data")
public class SeekerProfileRequestDTO {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(description = "First name of the seeker", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Schema(description = "Last name of the seeker", example = "Doe")
    private String lastName;

    @Schema(description = "Date of birth", example = "1990-01-01")
    private Date dateOfBirth;

    @Schema(description = "Current monthly salary", example = "5000.0")
    private Double currentMonthlySalary;

    @Schema(description = "CV URL", example = "https://example.com/cv.pdf")
    private String cvUrl;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^\\+[0-9]{10,15}$", message = "Contact number must be in international format (e.g., +380501234567)")
    @Schema(description = "Contact number in international format", example = "+380501234567")
    private String contactNumber;

    @Schema(description = "List of skills")
    private List<SeekerSkillSetDTO> skills;

    @Schema(description = "List of education details")
    private List<EducationDetailDTO> education;

    @Schema(description = "List of experience details")
    private List<ExperienceDetailDTO> experience;

    @Data
    public static class SeekerSkillSetDTO {
        @Schema(description = "Skill ID", example = "1")
        private Integer skillId;
        @Schema(description = "Skill level (1-5)", example = "5")
        private Short skillLevel;
        @Schema(description = "Description", example = "Java expert")
        private String description;
    }

    @Data
    public static class EducationDetailDTO {
        @Schema(description = "Degree ID", example = "1")
        private Integer certificateDegreeId;
        @Schema(description = "Major", example = "Computer Science")
        private String major;
        @Schema(description = "Institution/University name", example = "University of Kyiv")
        private String instituteOrUniversityName;
        @Schema(description = "Start date", example = "2010-09-01")
        private Date startDate;
        @Schema(description = "Completion date", example = "2014-06-30")
        private Date completionDate;
        @Schema(description = "CGPA", example = "90")
        private Integer cgpa;
    }

    @Data
    public static class ExperienceDetailDTO {
        @Schema(description = "Is current job", example = "true")
        private Boolean isCurrentJob;
        @Schema(description = "Start date", example = "2018-01-01")
        private Date startDate;
        @Schema(description = "End date", example = "2023-12-31")
        private Date endDate;
        @Schema(description = "Job title", example = "Senior Java Developer")
        private String jobTitle;
        @Schema(description = "Company name", example = "Tech Solutions")
        private String companyName;
        @Schema(description = "Job location city", example = "Kyiv")
        private String jobLocationCity;
        @Schema(description = "Job location country", example = "Ukraine")
        private String jobLocationCountry;
        @Schema(description = "Description", example = "Led development of enterprise applications")
        private String description;
    }
} 