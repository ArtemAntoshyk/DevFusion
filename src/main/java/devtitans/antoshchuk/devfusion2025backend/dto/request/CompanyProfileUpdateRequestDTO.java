package devtitans.antoshchuk.devfusion2025backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileUpdateRequestDTO {
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    private String name;

    @Size(min = 2, max = 100, message = "Business stream name must be between 2 and 100 characters")
    private String businessStreamName;

    @Size(max = 500, message = "Company logo URL must not exceed 500 characters")
    private String companyLogo;

    @Size(max = 2000, message = "Company description must not exceed 2000 characters")
    private String companyDescription;

    @Size(max = 500, message = "Company website URL must not exceed 500 characters")
    @Pattern(regexp = "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$", 
            message = "Invalid website URL format")
    private String companyWebsiteUrl;

    private LocalDate establishmentDate;

    private List<String> companyImages;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", 
            message = "Invalid phone number format. Use international format (e.g., +380501234567)")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 digits")
    private String contactNumber;
} 