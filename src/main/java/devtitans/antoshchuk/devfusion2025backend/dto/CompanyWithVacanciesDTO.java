package devtitans.antoshchuk.devfusion2025backend.dto;

import lombok.Data;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;


@Getter
@Setter
@Builder
public class CompanyWithVacanciesDTO {
    private Long id;
    private String logo;
    private String name;
    private List<VacancyDTO> recentVacancies;


    @Getter
    @Setter
    @Builder
    public static class VacancyDTO {
        private Long id;
        private String title;
        private String titleEn;
        private String employmentType;
        private String location;
        private String shortDescription;
        private String shortDescriptionEn;
        private RequiredExperienceDTO requiredExperience;
        private Set<TagDTO> tags;
        private Set<JobPostSkillDTO> skills;
        private String language;
    }
} 