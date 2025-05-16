package devtitans.antoshchuk.devfusion2025backend.util.mappers;

import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyAllInfoResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyBaseResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyWithPostsResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyDetailsDTO;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CompanyMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public CompanyMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
//        configureMappings(); // Винесли налаштування сюди
    }
    public CompanyAllInfoResponseDTO toAllInfoDTO(Company company) {
        CompanyAllInfoResponseDTO dto = modelMapper.map(company, CompanyAllInfoResponseDTO.class);
//        dto.setCompanyImages(
//                company.getCompanyImages().stream()
//                        .map(CompanyImage::getImageUrl) // припускаємо, що є getImageUrl()
//                        .collect(Collectors.toList())
//        );
        return dto;
    }

//    private void configureMappings() {
//        modelMapper.addMappings(new PropertyMap<Company, CompanyWithPostsResponseDTO>() {
//            @Override
//            protected void configure() {
//                map().setJobPostIds(source.getJobPosts().stream()
//                        .map(JobPost::getId)
//                        .collect(Collectors.toList()));
//            }
//        });
//    }

    public CompanyWithPostsResponseDTO companyToCompanyWithPostsResponseDTO(Company company) {
        CompanyWithPostsResponseDTO companyWithPostsResponseDTO = modelMapper.map(company, CompanyWithPostsResponseDTO.class);
        companyWithPostsResponseDTO.setJobPostIds(
                company.getJobPosts().stream().map(JobPost::getId).toList()
        );
        return companyWithPostsResponseDTO;
    }

    public CompanyBaseResponseDTO toBaseDTO(Company company) {
        return modelMapper.map(company, CompanyBaseResponseDTO.class);
    }

    public CompanyAllInfoResponseDTO companyToCompanyAllInfoResponseDTO(Company company) {
        return modelMapper.map(company, CompanyAllInfoResponseDTO.class);
    }

    public CompanyDetailsDTO toDetailsDTO(Company company) {
        if (company == null) {
            return null;
        }

        CompanyDetailsDTO dto = new CompanyDetailsDTO();
        dto.setId(company.getId());
        dto.setLogo(company.getLogo());
        dto.setName(company.getName());
        dto.setBusinessStreamName(company.getBusinessStreamName());
        dto.setContactNumber(company.getUser().getContactNumber());
        dto.setDescription(company.getCompanyDescription());

        return dto;
    }
}
