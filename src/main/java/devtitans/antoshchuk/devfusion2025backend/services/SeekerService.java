package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.dto.request.UserSeekerRegisterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.models.user.Seeker;
import devtitans.antoshchuk.devfusion2025backend.repositories.SeekerRepository;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.SeekerMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
