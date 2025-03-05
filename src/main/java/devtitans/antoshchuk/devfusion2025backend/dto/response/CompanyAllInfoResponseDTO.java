package devtitans.antoshchuk.devfusion2025backend.dto.response;

import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompanyAllInfoResponseDTO {
    private int id;
    private String name;
    private String businessStreamName;
    private String companyLogo;
    private String companyDescription;
    private Date establishmentDate;
    private String companyWebsiteUrl;
    private List<String> companyImages;
}
