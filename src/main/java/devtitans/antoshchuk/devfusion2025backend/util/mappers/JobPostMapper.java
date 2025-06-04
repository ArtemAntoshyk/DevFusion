package devtitans.antoshchuk.devfusion2025backend.util.mappers;

import devtitans.antoshchuk.devfusion2025backend.dto.JobPostSkillDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.RequiredExperienceDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.TagDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.*;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JobPostMapper {
    
    private final CompanyMapper companyMapper;

    @Autowired
    public JobPostMapper(CompanyMapper companyMapper) {
        this.companyMapper = companyMapper;
    }

    public JobPostResponseDTO toResponseDTO(JobPost jobPost) {
        if (jobPost == null) {
            return null;
        }

        return JobPostResponseDTO.builder()
            .id(jobPost.getId())
            .title(jobPost.getTitle())
            .titleEn(jobPost.getTitleEn())
            .description(jobPost.getJobDescription())
            .descriptionEn(jobPost.getJobDescriptionEn())
            .location(jobPost.getJobLocation())
            .salaryRange(jobPost.getSalary())
            .employmentType(jobPost.getJobType() != null ? jobPost.getJobType().getName() : null)
            .experienceLevel(jobPost.getJobGradation() != null ? jobPost.getJobGradation().getName() : null)
            .requiredExperience(jobPost.getExperience() != null ? RequiredExperienceDTO.builder()
                .id(jobPost.getExperience().getId())
                .experience(jobPost.getExperience().getExperience())
                .build() : null)
            .tags(jobPost.getTags().stream()
                .map(tag -> TagDTO.builder()
                    .id(tag.getId())
                    .name(tag.getName())
                    .build())
                .collect(Collectors.toSet()))
            .skills(jobPost.getJobPostSkillSets().stream()
                .map(skill -> JobPostSkillDTO.builder()
                    .id(skill.getSkill().getId())
                    .name(skill.getSkill().getName())
                    .level(skill.getSkillLevel())
                    .build())
                .collect(Collectors.toSet()))
            .language(jobPost.getLanguage())
            .company(mapCompanyToDTO(jobPost.getCompany()))
            .createdAt(jobPost.getCreatedDateTime())
            .isActive(jobPost.isActive())
            .build();
    }

    private JobPostResponseDTO.CompanyDTO mapCompanyToDTO(Company company) {
        if (company == null) {
            return null;
        }
        return JobPostResponseDTO.CompanyDTO.builder()
            .id(company.getId())
            .name(company.getName())
            .logo(company.getLogo())
            .build();
    }

    public JobPostDetailedResponseDTO toDetailedDTO(JobPost jobPost) {
        if (jobPost == null) {
            return null;
        }

        JobPostDetailedResponseDTO dto = new JobPostDetailedResponseDTO();
        dto.setId(jobPost.getId());
        dto.setTitle(jobPost.getTitle());
        dto.setTitleEn(jobPost.getTitleEn());
        dto.setJobLocation(jobPost.getJobLocation());
        dto.setJobDescription(jobPost.getJobDescription());
        dto.setJobDescriptionEn(jobPost.getJobDescriptionEn());
        dto.setCompanyNameHidden(jobPost.isCompanyNameHidden());
        dto.setCreatedDateTime(jobPost.getCreatedDateTime());
        dto.setActive(jobPost.isActive());
        dto.setSalary(jobPost.getSalary());
        dto.setLanguage(jobPost.getLanguage());
        
        // Map job type
        if (jobPost.getJobType() != null) {
            JobTypeDTO jobTypeDTO = new JobTypeDTO();
            jobTypeDTO.setId(jobPost.getJobType().getId());
            jobTypeDTO.setName(jobPost.getJobType().getName());
            dto.setJobType(jobTypeDTO);
        }

        // Map job gradation
        if (jobPost.getJobGradation() != null) {
            JobGradationDTO jobGradationDTO = new JobGradationDTO();
            jobGradationDTO.setId(jobPost.getJobGradation().getId());
            jobGradationDTO.setName(jobPost.getJobGradation().getName());
            dto.setJobGradation(jobGradationDTO);
        }

        // Map company
        if (jobPost.getCompany() != null) {
            dto.setCompany(companyMapper.toDetailsDTO(jobPost.getCompany()));
        }

        // Map required experience
        if (jobPost.getExperience() != null) {
            RequiredExperienceDTO experienceDTO = RequiredExperienceDTO.builder()
                .id(jobPost.getExperience().getId())
                .experience(jobPost.getExperience().getExperience())
                .build();
            dto.setRequiredExperience(experienceDTO);
        }

        // Map tags
        if (jobPost.getTags() != null) {
            Set<TagDTO> tagDTOs = jobPost.getTags().stream()
                .map(tag -> TagDTO.builder()
                    .id(tag.getId())
                    .name(tag.getName())
                    .build())
                .collect(Collectors.toSet());
            dto.setTags(tagDTOs);
        }

        // Map skills
        if (jobPost.getJobPostSkillSets() != null) {
            Set<JobPostSkillDTO> skillDTOs = jobPost.getJobPostSkillSets().stream()
                .map(skill -> JobPostSkillDTO.builder()
                    .id(skill.getSkill().getId())
                    .name(skill.getSkill().getName())
                    .build())
                .collect(Collectors.toSet());
            dto.setSkills(skillDTOs);
        }

        return dto;
    }

    private List<String> convertTagsToList(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(tags.split(","));
    }
} 