package devtitans.antoshchuk.devfusion2025backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompanyWithPostsResponseDTO {
    private int id;
    private String name;
    private String companyLogo;
    private List<Integer> jobPostIds;
}