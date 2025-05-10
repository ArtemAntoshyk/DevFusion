package devtitans.antoshchuk.devfusion2025backend.dto.request;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateRequestDTO {

    private String name;
    private String businessStreamName;
    private String companyLogo;
    private String companyDescription;
    private Date establishmentDate;
    private String companyWebsiteUrl;
    private List<String> companyImages;
}
