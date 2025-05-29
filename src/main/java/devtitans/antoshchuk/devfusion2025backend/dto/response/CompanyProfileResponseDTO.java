package devtitans.antoshchuk.devfusion2025backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileResponseDTO {
    private int id;
    private String name;
    private String businessStreamName;
    private String companyLogo;
    private String companyDescription;
    private String companyWebsiteUrl;
    private LocalDate establishmentDate;
    private List<String> companyImages;
    
    // User contact information
    private String email;
    private String contactNumber;
} 