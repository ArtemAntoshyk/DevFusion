package devtitans.antoshchuk.devfusion2025backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompanyBaseResponseDTO {
    private int id;
    private String name;
    private String companyLogo;
}

