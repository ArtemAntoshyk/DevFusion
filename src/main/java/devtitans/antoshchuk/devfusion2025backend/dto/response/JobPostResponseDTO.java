package devtitans.antoshchuk.devfusion2025backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Basic job post information")
public class JobPostResponseDTO {
    @Schema(description = "Job post ID", example = "1")
    private Integer id;

    @Schema(description = "Job title", example = "Senior Java Developer")
    private String title;

    @Schema(description = "Job description", example = "We are looking for an experienced Java developer...")
    private String description;

    @Schema(description = "Location", example = "London, UK")
    private String location;

    @Schema(description = "Job requirements", example = "- 5+ years of Java experience\n- Knowledge of Spring Framework")
    private String requirements;
    
    @Schema(description = "Company information")
    private CompanyDTO company;
    
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