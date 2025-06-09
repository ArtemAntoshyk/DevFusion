package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.dto.request.CompanyProfileUpdateRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyAllInfoResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyBaseResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyWithPostsResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.PaginatedCompanyResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyProfileResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import devtitans.antoshchuk.devfusion2025backend.models.user.CompanyImage;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.repositories.CompanyRepository;
import devtitans.antoshchuk.devfusion2025backend.repositories.UserAccountRepository;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.CompanyMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
import java.io.IOException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.util.Date;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserAccountRepository userAccountRepository;
    private CompanyMapper companyMapper;
    @Autowired
    private AmazonS3 amazonS3;
    @Autowired
    private String s3BucketName;
    @Autowired
    public CompanyService(CompanyRepository companyRepository, UserAccountRepository userAccountRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.userAccountRepository = userAccountRepository;
        this.companyMapper = companyMapper;
    }

    @Transactional
    public Company save(Company company) {
        return companyRepository.save(company);
    }

    @Transactional
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Transactional
    public CompanyAllInfoResponseDTO getCompanyById(int id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company with ID " + id + " not found"));
        return companyMapper.toAllInfoDTO(company);
    }

    @Transactional
    public List<CompanyWithPostsResponseDTO> getAllCompaniesWithPostsDTOs() {
        List<Company> companies = companyRepository.findAll().stream()
                .distinct()
                .toList();
        return companies.stream()
                .map(company -> companyMapper.companyToCompanyWithPostsResponseDTO(company))
                .toList();
    }

    @Transactional
    public List<CompanyBaseResponseDTO> getAllCompaniesBaseInfoDTOs() {
        return companyRepository.findAllCompaniesBasic().stream()
                .map(company -> companyMapper.toBaseDTO(company))
                .toList();
    }

    public PaginatedCompanyResponseDTO getFilteredCompanies(Pageable pageable, String search, String businessStream) {
        Page<Company> companyPage = companyRepository.findFilteredCompanies(search, businessStream, pageable);

        List<CompanyBaseResponseDTO> dtos = companyPage.getContent()
                .stream()
                .map(companyMapper::toBaseDTO)
                .toList();

        return new PaginatedCompanyResponseDTO(
                dtos,
                companyPage.getTotalElements(),
                companyPage.getTotalPages(),
                companyPage.getNumber(),
                companyPage.getSize()
        );
    }

    public Page<CompanyBaseResponseDTO> getAllCompaniesBasicInfo(Pageable pageable) {
        Page<Company> companiesPage = companyRepository.findAll(pageable);
        return companiesPage.map(companyMapper::toBaseDTO);
    }

    public CompanyWithPostsResponseDTO getCompanyWithPostsById(int id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company with ID " + id + " not found"));
        return companyMapper.companyToCompanyWithPostsResponseDTO(company);
    }

    public Boolean existsByName(String name) {
        return companyRepository.existsByName(name);
    }

    public void deleteCompany(int id) {
        companyRepository.deleteById(id);
    }

    @Transactional
    public CompanyProfileResponseDTO getCompanyProfile(int userAccountId) {
        log.debug("Attempting to get company profile for user ID: {}", userAccountId);
        
        UserAccount userAccount = userAccountRepository.findById(userAccountId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Company company = companyRepository.findByUserId(userAccountId);
        if (company == null) {
            log.info("Creating new company profile for user ID: {}", userAccountId);
            // Create a new company profile if it doesn't exist
            company = new Company();
            company.setUser(userAccount);
            company = companyRepository.save(company);
            userAccount.setCompany(company);
            userAccountRepository.save(userAccount);
        }

        log.debug("Found company with ID: {} for user ID: {}", company.getId(), userAccountId);
        CompanyProfileResponseDTO dto = new CompanyProfileResponseDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setBusinessStreamName(company.getBusinessStreamName());
        dto.setCompanyLogo(company.getLogo());
        dto.setCompanyDescription(company.getCompanyDescription());
        dto.setCompanyWebsiteUrl(company.getCompanyWebsiteUrl());
        dto.setEstablishmentDate(company.getEstablishmentDate());
        dto.setCompanyImages(company.getCompanyImages().stream()
            .map(CompanyImage::getCompany_image)
            .collect(Collectors.toList()));
        dto.setEmail(company.getUser().getEmail());
        dto.setContactNumber(company.getUser().getContactNumber());
        log.debug("Successfully mapped company to DTO for user ID: {}", userAccountId);
        return dto;
    }

    @Transactional
    public CompanyProfileResponseDTO updateCompanyProfile(int userAccountId, CompanyProfileUpdateRequestDTO updateRequest) {
        log.debug("Attempting to update company profile for user ID: {}", userAccountId);
        
        UserAccount userAccount = userAccountRepository.findById(userAccountId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        final Company company;
        Company existingCompany = companyRepository.findByUserId(userAccountId);
        if (existingCompany == null) {
            log.info("Creating new company profile for user ID: {}", userAccountId);
            // Create a new company profile if it doesn't exist
            company = new Company();
            company.setUser(userAccount);
            userAccount.setCompany(company);
        } else {
            company = existingCompany;
        }

        log.debug("Found company with ID: {} for user ID: {}", company.getId(), userAccountId);

        // Update company information
        if (updateRequest.getName() != null) {
            log.debug("Updating company name from '{}' to '{}'", company.getName(), updateRequest.getName());
            company.setName(updateRequest.getName());
        }
        if (updateRequest.getBusinessStreamName() != null) {
            log.debug("Updating business stream from '{}' to '{}'", company.getBusinessStreamName(), updateRequest.getBusinessStreamName());
            company.setBusinessStreamName(updateRequest.getBusinessStreamName());
        }
        if (updateRequest.getCompanyDescription() != null) {
            log.debug("Updating company description");
            company.setCompanyDescription(updateRequest.getCompanyDescription());
        }
        if (updateRequest.getCompanyWebsiteUrl() != null) {
            log.debug("Updating company website URL from '{}' to '{}'", company.getCompanyWebsiteUrl(), updateRequest.getCompanyWebsiteUrl());
            company.setCompanyWebsiteUrl(updateRequest.getCompanyWebsiteUrl());
        }
        if (updateRequest.getEstablishmentDate() != null) {
            log.debug("Updating establishment date from '{}' to '{}'", company.getEstablishmentDate(), updateRequest.getEstablishmentDate());
            company.setEstablishmentDate(updateRequest.getEstablishmentDate());
        }

        // companyLogo upload
        MultipartFile logoFile = updateRequest.getCompanyLogo();
        if (logoFile != null && !logoFile.isEmpty()) {
            String logoKey = "company/logo/" + company.getId() + "/" + UUID.randomUUID() + "_" + logoFile.getOriginalFilename();
            try {
                amazonS3.putObject(s3BucketName, logoKey, logoFile.getInputStream(), null);
                // Генерируем pre-signed URL с Content-Disposition: inline
                GeneratePresignedUrlRequest logoUrlRequest = new GeneratePresignedUrlRequest(s3BucketName, logoKey)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)); // 7 дней
                logoUrlRequest.addRequestParameter("response-content-disposition", "inline");
                String logoUrl = amazonS3.generatePresignedUrl(logoUrlRequest).toString();
                company.setLogo(logoUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload company logo", e);
            }
        }

        // companyImages upload
        List<MultipartFile> imageFiles = updateRequest.getCompanyImages();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            company.getCompanyImages().clear();
            for (MultipartFile imageFile : imageFiles) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    String imageKey = "company/images/" + company.getId() + "/" + UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                    try {
                        amazonS3.putObject(s3BucketName, imageKey, imageFile.getInputStream(), null);
                        // Генерируем pre-signed URL с Content-Disposition: inline
                        GeneratePresignedUrlRequest imageUrlRequest = new GeneratePresignedUrlRequest(s3BucketName, imageKey)
                                .withMethod(HttpMethod.GET)
                                .withExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)); // 7 дней
                        imageUrlRequest.addRequestParameter("response-content-disposition", "inline");
                        String imageUrl = amazonS3.generatePresignedUrl(imageUrlRequest).toString();
                        CompanyImage image = new CompanyImage(company, imageUrl);
                        company.addCompanyImage(image);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to upload company image", e);
                    }
                }
            }
        }

        // Update user contact information
        if (updateRequest.getEmail() != null) {
            log.debug("Updating user email from '{}' to '{}'", userAccount.getEmail(), updateRequest.getEmail());
            userAccount.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getContactNumber() != null) {
            log.debug("Updating contact number from '{}' to '{}'", userAccount.getContactNumber(), updateRequest.getContactNumber());
            userAccount.setContactNumber(updateRequest.getContactNumber());
        }

        // Save changes
        log.debug("Saving updated company profile");
        Company savedCompany = companyRepository.save(company);
        userAccountRepository.save(userAccount);
        log.debug("Successfully saved company profile with ID: {}", savedCompany.getId());

        return getCompanyProfile(userAccountId);
    }
}
