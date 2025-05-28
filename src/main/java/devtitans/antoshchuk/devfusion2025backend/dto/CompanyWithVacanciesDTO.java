package devtitans.antoshchuk.devfusion2025backend.dto;

import lombok.Data;
import lombok.Builder;

import java.util.List;

@Data
@Builder
public class CompanyWithVacanciesDTO {
    private Long id;
    private String logo;
    private String name;
    private List<VacancyDTO> recentVacancies;

    @Data
    @Builder
    public static class VacancyDTO {
        private Long id;
        private String title;
        private String employmentType;
        private String location;
        private String shortDescription;
    }
} 