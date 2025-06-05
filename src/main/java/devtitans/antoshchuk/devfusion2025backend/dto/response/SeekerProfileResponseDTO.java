package devtitans.antoshchuk.devfusion2025backend.dto.response;

import devtitans.antoshchuk.devfusion2025backend.models.user.EducationDetail;
import devtitans.antoshchuk.devfusion2025backend.models.user.ExperienceDetail;
import devtitans.antoshchuk.devfusion2025backend.models.user.SeekerSkillSet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
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

    @Schema(description = "List of skills", example = """
        [
            {
                "skillName": "Java",
                "proficiencyLevel": "EXPERT"
            },
            {
                "skillName": "Spring",
                "proficiencyLevel": "ADVANCED"
            }
        ]
        """)
    private List<SeekerSkillSet> skills;

    @Schema(description = "List of education details", example = """
        [
            {
                "degree": "Bachelor's",
                "major": "Computer Science",
                "institution": "University of Kyiv",
                "startDate": "2010-09-01",
                "endDate": "2014-06-30"
            }
        ]
        """)
    private List<EducationDetail> education;

    @Schema(description = "List of experience details", example = """
        [
            {
                "jobTitle": "Senior Java Developer",
                "companyName": "Tech Solutions",
                "startDate": "2018-01-01",
                "endDate": "2023-12-31",
                "description": "Led development of enterprise applications"
            }
        ]
        """)
    private List<ExperienceDetail> experience;

    @Schema(description = "Registration date", example = "2024-01-01T00:00:00")
    private Date registrationDate;
} 