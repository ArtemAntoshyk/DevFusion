package devtitans.antoshchuk.devfusion2025backend.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedCompanyResponseDTO {
    private List<CompanyBaseResponseDTO> companies;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
