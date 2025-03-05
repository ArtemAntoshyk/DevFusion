package devtitans.antoshchuk.devfusion2025backend.util.mappers;

import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyWithPostsResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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

}
