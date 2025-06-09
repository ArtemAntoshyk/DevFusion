package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.dto.request.SeekerProfileRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.SeekerProfileResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.exceptions.ResourceNotFoundException;
import devtitans.antoshchuk.devfusion2025backend.models.user.Seeker;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.models.user.SeekerSkillSet;
import devtitans.antoshchuk.devfusion2025backend.models.user.EducationDetail;
import devtitans.antoshchuk.devfusion2025backend.models.user.ExperienceDetail;
import devtitans.antoshchuk.devfusion2025backend.models.user.CertificateDegree;
import devtitans.antoshchuk.devfusion2025backend.repositories.SeekerRepository;
import devtitans.antoshchuk.devfusion2025backend.repositories.UserAccountRepository;
import devtitans.antoshchuk.devfusion2025backend.repositories.SkillRepository;
import devtitans.antoshchuk.devfusion2025backend.repositories.CertificateDegreeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.util.UUID;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.util.Date;
import java.util.stream.Collectors;
import devtitans.antoshchuk.devfusion2025backend.dto.response.SeekerProfileResponseDTO.SeekerSkillSetDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.SeekerProfileResponseDTO.EducationDetailDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.SeekerProfileResponseDTO.ExperienceDetailDTO;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class SeekerProfileService {

    private final SeekerRepository seekerRepository;
    private final UserAccountRepository userAccountRepository;
    private final SkillRepository skillRepository;
    private final CertificateDegreeRepository certificateDegreeRepository;
    @Autowired
    private AmazonS3 amazonS3;
    @Autowired
    private String s3BucketName;

    @Transactional(readOnly = true)
    public SeekerProfileResponseDTO getSeekerProfile(Integer userId) {
        UserAccount userAccount = userAccountRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userAccount.getSeeker() == null) {
            // Create a new seeker profile if it doesn't exist
            Seeker seeker = new Seeker();
            seeker.setUserAccount(userAccount);
            seeker = seekerRepository.save(seeker);
            userAccount.setSeeker(seeker);
            userAccountRepository.save(userAccount);
            return mapToResponseDTO(seeker);
        }

        return mapToResponseDTO(userAccount.getSeeker());
    }

    @Transactional
    public SeekerProfileResponseDTO updateSeekerProfile(Integer userId, SeekerProfileRequestDTO requestDTO) {
        UserAccount userAccount = userAccountRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Seeker seeker = userAccount.getSeeker();
        if (seeker == null) {
            // Create a new seeker profile if it doesn't exist
            seeker = new Seeker();
            seeker.setUserAccount(userAccount);
            userAccount.setSeeker(seeker);
        }

        updateSeekerFromDTO(seeker, requestDTO);
        updateUserAccountFromDTO(userAccount, requestDTO);

        seeker = seekerRepository.save(seeker);
        userAccountRepository.save(userAccount);

        return mapToResponseDTO(seeker);
    }

    private SeekerProfileResponseDTO mapToResponseDTO(Seeker seeker) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SeekerProfileResponseDTO dto = new SeekerProfileResponseDTO();
        dto.setId(seeker.getId());
        dto.setFirstName(seeker.getFirstName());
        dto.setLastName(seeker.getLastName());
        dto.setDateOfBirth(seeker.getDateOfBirth());
        dto.setCurrentMonthlySalary(seeker.getCurrentMonthlySalary());
        dto.setCvUrl(seeker.getCvUrl());
        dto.setEmail(seeker.getUserAccount().getEmail());
        dto.setContactNumber(seeker.getUserAccount().getContactNumber());
        dto.setSeekerTitle(seeker.getSeekerTitle());
        // skills
        dto.setSkills(seeker.getSeekerSkillSets() == null ? null : seeker.getSeekerSkillSets().stream()
            .map(skillSet -> new SeekerSkillSetDTO(
                skillSet.getSkill() != null ? skillSet.getSkill().getId() : null,
                skillSet.getSkillLevel(),
                skillSet.getDescription()
            )).collect(Collectors.toList()));
        // education
        dto.setEducation(seeker.getEducationDetails() == null ? null : seeker.getEducationDetails().stream()
            .map(edu -> new EducationDetailDTO(
                edu.getCertificateDegree() != null ? edu.getCertificateDegree().getId() : null,
                edu.getMajor(),
                edu.getInstituteOrUniversityName(),
                edu.getStartDate() != null ? dateFormat.format(edu.getStartDate()) : null,
                edu.getCompletionDate() != null ? dateFormat.format(edu.getCompletionDate()) : null,
                edu.getCgpa()
            )).collect(Collectors.toList()));
        // experience
        dto.setExperience(seeker.getExperienceDetails() == null ? null : seeker.getExperienceDetails().stream()
            .map(exp -> new ExperienceDetailDTO(
                exp.isCurrentJob(),
                exp.getStartDate() != null ? dateFormat.format(exp.getStartDate()) : null,
                exp.getEndDate() != null ? dateFormat.format(exp.getEndDate()) : null,
                exp.getJobTitle(),
                exp.getCompanyName(),
                exp.getJobLocationCity(),
                exp.getJobLocationCounty(),
                exp.getDescription()
            )).collect(Collectors.toList()));
        dto.setRegistrationDate(seeker.getUserAccount().getRegistrationDate());
        return dto;
    }

    private String skillLevelToString(Number level) {
        // Можно заменить на свой маппинг
        switch (level.intValue()) {
            case 5: return "EXPERT";
            case 4: return "ADVANCED";
            case 3: return "INTERMEDIATE";
            case 2: return "BEGINNER";
            case 1: return "NOVICE";
            default: return String.valueOf(level);
        }
    }

    private void updateSeekerFromDTO(Seeker seeker, SeekerProfileRequestDTO dto) {
        seeker.setFirstName(dto.getFirstName());
        seeker.setLastName(dto.getLastName());
        seeker.setDateOfBirth(dto.getDateOfBirth());
        seeker.setCurrentMonthlySalary(dto.getCurrentMonthlySalary());
        seeker.setCvUrl(dto.getCvUrl());
        seeker.setSeekerTitle(dto.getSeekerTitle());

        // Update skills
        if (dto.getSkills() != null) {
            seeker.getSeekerSkillSets().clear();
            for (var skillDto : dto.getSkills()) {
                var skill = skillRepository.findById(skillDto.getSkillId()).orElse(null);
                if (skill != null) {
                    var skillSet = new SeekerSkillSet();
                    skillSet.setSeeker(seeker);
                    skillSet.setSkill(skill);
                    skillSet.setSkillLevel(skillDto.getSkillLevel());
                    skillSet.setDescription(skillDto.getDescription());
                    seeker.getSeekerSkillSets().add(skillSet);
                }
            }
        }
        // Update education
        if (dto.getEducation() != null) {
            seeker.getEducationDetails().clear();
            for (var eduDto : dto.getEducation()) {
                var edu = new EducationDetail();
                edu.setSeeker(seeker);
                if (eduDto.getCertificateDegreeId() != null) {
                    CertificateDegree degree = certificateDegreeRepository.findById(eduDto.getCertificateDegreeId()).orElse(null);
                    edu.setCertificateDegree(degree);
                }
                edu.setMajor(eduDto.getMajor());
                edu.setInstituteOrUniversityName(eduDto.getInstituteOrUniversityName());
                edu.setStartDate(eduDto.getStartDate());
                edu.setCompletionDate(eduDto.getCompletionDate());
                if (eduDto.getCgpa() != null) {
                    edu.setCgpa(eduDto.getCgpa());
                }
                seeker.getEducationDetails().add(edu);
            }
        }
        // Update experience
        if (dto.getExperience() != null) {
            seeker.getExperienceDetails().clear();
            for (var expDto : dto.getExperience()) {
                var exp = new ExperienceDetail();
                exp.setSeeker(seeker);
                exp.setCurrentJob(Boolean.TRUE.equals(expDto.getIsCurrentJob()));
                exp.setStartDate(expDto.getStartDate());
                exp.setEndDate(expDto.getEndDate());
                exp.setJobTitle(expDto.getJobTitle());
                exp.setCompanyName(expDto.getCompanyName());
                exp.setJobLocationCity(expDto.getJobLocationCity());
                exp.setJobLocationCounty(expDto.getJobLocationCountry());
                exp.setDescription(expDto.getDescription());
                seeker.getExperienceDetails().add(exp);
            }
        }
    }

    private void updateUserAccountFromDTO(UserAccount userAccount, SeekerProfileRequestDTO dto) {
        userAccount.setEmail(dto.getEmail());
        userAccount.setContactNumber(dto.getContactNumber());
    }

    public String uploadSeekerCv(Integer userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (!file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }
        UserAccount userAccount = userAccountRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Seeker seeker = userAccount.getSeeker();
        if (seeker == null) {
            seeker = new Seeker();
            seeker.setUserAccount(userAccount);
            userAccount.setSeeker(seeker);
        }
        String key = "cv/" + userId + "/" + UUID.randomUUID() + ".pdf";
        amazonS3.putObject(s3BucketName, key, file.getInputStream(), null);
        // Генерируем pre-signed URL с Content-Disposition: inline
        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(s3BucketName, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)); // 7 дней
        urlRequest.addRequestParameter("response-content-disposition", "inline");
        String fileUrl = amazonS3.generatePresignedUrl(urlRequest).toString();
        seeker.setCvUrl(fileUrl);
        seekerRepository.save(seeker);
        return fileUrl;
    }
} 