package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyAllInfoResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyBaseResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyWithPostsResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.PaginatedCompanyResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.services.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
@Tag(name = "Company Management", description = "Endpoints for managing companies")
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    @Operation(summary = "Get all companies with basic information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved companies",
                    content = @Content(schema = @Schema(implementation = CompanyBaseResponseDTO.class)))
    })
    public ResponseEntity<List<CompanyBaseResponseDTO>> getAllCompaniesBasic() {
        return ResponseEntity.ok(companyService.getAllCompaniesBaseInfoDTOs());
    }

    @GetMapping("/with-posts")
    @Operation(summary = "Get all companies with their job posts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved companies with posts",
                    content = @Content(schema = @Schema(implementation = CompanyWithPostsResponseDTO.class)))
    })
    public ResponseEntity<List<CompanyWithPostsResponseDTO>> getAllCompaniesWithPosts() {
        return ResponseEntity.ok(companyService.getAllCompaniesWithPostsDTOs());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get company by ID with all information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved company",
                    content = @Content(schema = @Schema(implementation = CompanyAllInfoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<CompanyAllInfoResponseDTO> getCompanyById(
            @Parameter(description = "Company ID") @PathVariable int id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    @GetMapping("/{id}/with-posts")
    @Operation(summary = "Get company by ID with its job posts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved company with posts",
                    content = @Content(schema = @Schema(implementation = CompanyWithPostsResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<CompanyWithPostsResponseDTO> getCompanyWithPostsById(
            @Parameter(description = "Company ID") @PathVariable int id) {
        return ResponseEntity.ok(companyService.getCompanyWithPostsById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search and filter companies with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered companies",
                    content = @Content(schema = @Schema(implementation = PaginatedCompanyResponseDTO.class)))
    })
    public ResponseEntity<PaginatedCompanyResponseDTO> searchCompanies(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Search term for company name") @RequestParam(required = false) String search,
            @Parameter(description = "Business stream filter") @RequestParam(required = false) String businessStream) {
        
        Pageable pageable = PageRequest.of(page, size);
        PaginatedCompanyResponseDTO result = companyService.getFilteredCompanies(pageable, search, businessStream);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Company successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<Void> deleteCompany(
            @Parameter(description = "Company ID") @PathVariable int id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
} 