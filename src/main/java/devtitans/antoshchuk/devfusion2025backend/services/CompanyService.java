package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyAllInfoResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyBaseResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyWithPostsResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.PaginatedCompanyResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import devtitans.antoshchuk.devfusion2025backend.repositories.CompanyRepository;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.CompanyMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
}
