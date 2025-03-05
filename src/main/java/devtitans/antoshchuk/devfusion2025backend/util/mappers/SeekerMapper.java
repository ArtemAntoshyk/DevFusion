package devtitans.antoshchuk.devfusion2025backend.util.mappers;

import devtitans.antoshchuk.devfusion2025backend.dto.request.UserSeekerRegisterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import devtitans.antoshchuk.devfusion2025backend.models.user.Seeker;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SeekerMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public SeekerMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
//        configureMappings(); // Винесли налаштування сюди
    }

//    private void configureMappings() {
//        modelMapper.addMappings(new PropertyMap<Seeker, SeekerWithPostsResponseDTO>() {
//            @Override
//            protected void configure() {
//                map().setJobPostIds(source.getJobPosts().stream()
//                        .map(JobPost::getId)
//                        .collect(Collectors.toList()));
//            }
//        });
//    }

    public UserSeekerRegisterRequestDTO seekerToSeekerResponseDTO(Seeker seeker) {
        UserSeekerRegisterRequestDTO seekerWithPostsResponseDTO = modelMapper.map(seeker, UserSeekerRegisterRequestDTO.class);
        return seekerWithPostsResponseDTO;
    }

}
