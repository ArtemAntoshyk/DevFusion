package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.dto.CompanyWithVacanciesDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.RequiredExperienceDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.TagDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.JobPostSkillDTO;
import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import devtitans.antoshchuk.devfusion2025backend.repositories.CompanyRepository;
import devtitans.antoshchuk.devfusion2025backend.repositories.JobPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyStatisticsService {

    private final CompanyRepository companyRepository;
    private final JobPostRepository jobPostRepository;

    @Transactional(readOnly = true)
    public List<CompanyWithVacanciesDTO> getTopCompaniesWithVacancies() {
        return companyRepository.findTopCompaniesByVacancyCount(3)
                .stream()
                .limit(3)
                .map(result -> {
                    Company company = (Company) result[0];
                    List<JobPost> recentPosts = jobPostRepository.findTop3ByCompanyOrderByCreatedDateTimeDesc(company);
                    
                    return CompanyWithVacanciesDTO.builder()
                            .id(company.getId().longValue())
                            .logo(company.getLogo())
                            .name(company.getName())
                            .recentVacancies(recentPosts.stream()
                                    .map(post -> CompanyWithVacanciesDTO.VacancyDTO.builder()
                                            .id(post.getId().longValue())
                                            .title(post.getTitle())
                                            .titleEn(post.getTitleEn())
                                            .employmentType(post.getJobType().getName())
                                            .location(post.getJobLocation())
                                            .shortDescription(truncateDescription(post.getJobDescription(), 200))
                                            .shortDescriptionEn(truncateDescription(post.getJobDescriptionEn(), 200))
                                            .requiredExperience(post.getExperience() != null ? RequiredExperienceDTO.builder()
                                                    .id(post.getExperience().getId())
                                                    .experience(post.getExperience().getExperience())
                                                    .build() : null)
                                            .tags(post.getTags().stream()
                                                    .<TagDTO>map(tag -> TagDTO.builder()
                                                            .id(tag.getId())
                                                            .name(tag.getName())
                                                            .build())
                                                    .collect(Collectors.toSet()))
                                            .skills(post.getJobPostSkillSets().stream()
                                                    .<JobPostSkillDTO>map(skill -> JobPostSkillDTO.builder()
                                                            .id(skill.getSkill().getId())
                                                            .name(skill.getSkill().getName())
                                                            .level(skill.getSkillLevel())
                                                            .build())
                                                    .collect(Collectors.toSet()))
                                            .language(post.getLanguage())
                                            .build())
                                    .collect(Collectors.toList()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private String truncateDescription(String description, int maxLength) {
        if (description == null || description.length() <= maxLength) {
            return description;
        }
        return description.substring(0, maxLength);
    }
} 