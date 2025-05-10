package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.dto.request.CompanyCreateRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.request.CompanyUpdateRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyAllInfoResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyBaseResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyWithPostsResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.PaginatedCompanyResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.CompanyMapper;
import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import devtitans.antoshchuk.devfusion2025backend.repositiories.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompanyService {
    private CompanyRepository companyRepository;
    private CompanyMapper companyMapper;
    @Autowired
    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
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
        List<Company> companies = getAllCompanies();
        return companies.stream()
                .map(company -> companyMapper.companyToCompanyWithPostsResponseDTO(company))
                .toList();
    }

    @Transactional
    public List<CompanyBaseResponseDTO> getAllCompaniesBaseInfoDTOs() {
        List<Company> companies = getAllCompanies();
        return companies.stream()
                .map(company -> companyMapper.toBaseDTO(company))
                .toList();
    }

    public Company createCompany(@Valid CompanyCreateRequestDTO requestDTO) {
        Company company = new Company();
        company.setName(requestDTO.getName());
        company.setBusinessStreamName(requestDTO.getBusinessStreamName());
        company.setCompanyLogo(requestDTO.getCompanyLogo());
        company.setCompanyDescription(requestDTO.getCompanyDescription());
        company.setEstablishmentDate(requestDTO.getEstablishmentDate());
        company.setCompanyWebsiteUrl(requestDTO.getCompanyWebsiteUrl());
        // У разі наявності CompanyImage — логіку збереження окремо треба додати тут

        return companyRepository.save(company);
    }

    public PaginatedCompanyResponseDTO getFilteredCompanies(Pageable pageable, String search, String businessStream) {

        Page<Company> companyPage = companyRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"));
            }

            if (businessStream != null && !businessStream.trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("businessStreamName")), businessStream.toLowerCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);

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
    public CompanyAllInfoResponseDTO updateCompany(int id, @Valid CompanyUpdateRequestDTO request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company with ID " + id + " not found"));

        company.setName(request.getName());
        company.setBusinessStreamName(request.getBusinessStreamName());
        company.setCompanyLogo(request.getCompanyLogo());
        company.setCompanyDescription(request.getCompanyDescription());
        company.setEstablishmentDate(request.getEstablishmentDate());
        company.setCompanyWebsiteUrl(request.getCompanyWebsiteUrl());

        Company updatedCompany = companyRepository.save(company);
        return companyMapper.companyToCompanyAllInfoResponseDTO(updatedCompany);
    }


}
