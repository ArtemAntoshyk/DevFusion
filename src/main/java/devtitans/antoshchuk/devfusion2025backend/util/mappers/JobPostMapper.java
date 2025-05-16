package devtitans.antoshchuk.devfusion2025backend.util.mappers;

import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyDetailsDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobGradationDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostDetailedResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobTypeDTO;
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

    public JobPostDetailedResponseDTO toDetailedDTO(JobPost jobPost) {
        if (jobPost == null) {
            return null;
        }

        JobPostDetailedResponseDTO dto = new JobPostDetailedResponseDTO();
        dto.setId(jobPost.getId());
        dto.setTitle(jobPost.getTitle());
        dto.setJobLocation(jobPost.getJobLocation());
        dto.setJobDescription(jobPost.getJobDescription());
        dto.setCompanyNameHidden(jobPost.isCompanyNameHidden());
        dto.setCreatedDateTime(jobPost.getCreatedDateTime());
        dto.setActive(jobPost.isActive());
        
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
        dto.setCompany(companyMapper.toDetailsDTO(jobPost.getCompany()));

        return dto;
    }

    private List<String> convertTagsToList(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(tags.split(","));
    }
} 