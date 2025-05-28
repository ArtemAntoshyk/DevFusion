package devtitans.antoshchuk.devfusion2025backend.util.mappers;

import devtitans.antoshchuk.devfusion2025backend.dto.response.*;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class JobPostMapper {
    
    private final CompanyMapper companyMapper;

    @Autowired
    public JobPostMapper(CompanyMapper companyMapper) {
        this.companyMapper = companyMapper;
    }

    public JobPostResponseDTO toResponseDTO(JobPost jobPost) {
        return JobPostResponseDTO.builder()
            .id(jobPost.getId())
            .title(jobPost.getTitle())
            .description(jobPost.getJobDescription())
            .location(jobPost.getJobLocation())
            .company(mapCompanyToDTO(jobPost.getCompany()))
            .build();
    }

    private JobPostResponseDTO.CompanyDTO mapCompanyToDTO(Company company) {
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

        return dto;
    }

    private List<String> convertTagsToList(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(tags.split(","));
    }
} 