package devtitans.antoshchuk.devfusion2025backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Detailed information about a company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDetailsDTO {
    @Schema(description = "Company ID", example = "1")
    private Integer id;

    @Schema(description = "URL to company logo", example = "https://example.com/logo.png")
    private String logo;

    @Schema(description = "Company name", example = "Tech Solutions Ltd")
    private String name;

    @Schema(description = "Company's business domain", example = "Information Technology")
    private String businessStreamName;

    @Schema(description = "Company contact number", example = "+380501234567")
    private String contactNumber;

    @Schema(description = "Detailed company description")
    private String description;
} 