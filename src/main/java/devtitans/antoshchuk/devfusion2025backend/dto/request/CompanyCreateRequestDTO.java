package devtitans.antoshchuk.devfusion2025backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCreateRequestDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String businessStreamName;
    private String companyLogo;
    private String companyDescription;
    @NotNull
    private Date establishmentDate;
    private String companyWebsiteUrl;
    private List<String> companyImages;
}
