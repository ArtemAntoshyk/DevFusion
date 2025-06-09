package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.dto.request.UserSeekerRegisterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.models.user.Seeker;
import devtitans.antoshchuk.devfusion2025backend.repositories.SeekerRepository;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.SeekerMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import devtitans.antoshchuk.devfusion2025backend.dto.response.SeekerProfileResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import devtitans.antoshchuk.devfusion2025backend.specifications.SeekerSpecification;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;

@Service
public class SeekerService {
    private SeekerRepository seekerRepository;
    private SeekerMapper seekerMapper;
    @Autowired
    public SeekerService(SeekerRepository seekerRepository, SeekerMapper seekerMapper) {
        this.seekerRepository = seekerRepository;
        this.seekerMapper = seekerMapper;
    }
    @Transactional
    public List<Seeker> getAllCompanies() {
        return seekerRepository.findAll();
    }
    @Transactional
    public Optional<Seeker> getSeekerById(int id) {
        return seekerRepository.findById(id);
    }
    @Transactional
    public List<UserSeekerRegisterRequestDTO> getSeekersRegistersRequestDTOs() {
        List<Seeker> companies = getAllCompanies();
        return companies.stream()
                .map(seeker -> seekerMapper.seekerToSeekerResponseDTO(seeker))
                .toList();
    }

    public Seeker createSeeker(Seeker seeker) {
        return seekerRepository.save(seeker);
    }

    public Page<SeekerProfileResponseDTO> searchSeekers(String query, List<Integer> skillIds, Pageable pageable) {
        var spec = SeekerSpecification.filterByQueryAndSkills(query, skillIds);
        return seekerRepository.findAll(spec, pageable)
                .map(seeker -> seekerMapper.toProfileResponseDTO(seeker));
    }

    public SeekerProfileResponseDTO getSeekerProfileResponseDTO(Seeker seeker) {
        return seekerMapper.toProfileResponseDTO(seeker);
    }
}
