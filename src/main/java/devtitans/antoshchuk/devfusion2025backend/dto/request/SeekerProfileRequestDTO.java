package devtitans.antoshchuk.devfusion2025backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

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
} 