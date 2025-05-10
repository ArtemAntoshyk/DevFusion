package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.request.CompanyCreateRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.request.CompanyUpdateRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.*;
import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import devtitans.antoshchuk.devfusion2025backend.services.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
@Tag(name = "Company Management", description = "API for managing companies and their information")
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Operation(
            summary = "Get paginated list of companies",
            description = "Returns a paginated list of companies with optional filtering and sorting"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved companies",
                    content = @Content(schema = @Schema(implementation = PaginatedCompanyResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters supplied")
    })
    @GetMapping
    @Cacheable(value = "companies", key = "{#pageable.pageNumber, #pageable.pageSize, #pageable.sort, #search, #businessStream}")
    public ResponseEntity<PaginatedCompanyResponseDTO> getAllCompanies(
            @Parameter(description = "Pagination and sorting parameters",
                    example = "{\"page\":0,\"size\":10,\"sort\":[\"id,asc\"]}")
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,

            @Parameter(description = "Search term for company name (case-insensitive)")
            @RequestParam(required = false) String search,

            @Parameter(description = "Filter by business stream name")
            @RequestParam(required = false) String businessStream) {

        return ResponseEntity.ok(companyService.getFilteredCompanies(pageable, search, businessStream));
    }

    @Operation(summary = "Get basic company information",
            description = "Returns paginated list of companies with minimal information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved companies",
                    content = @Content(schema = @Schema(implementation = CompanyBaseResponseDTO.class)))
    })
    @GetMapping("/basic")
    public ResponseEntity<Page<CompanyBaseResponseDTO>> getAllCompaniesBasicInfo(
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(companyService.getAllCompaniesBasicInfo(pageable));
    }

    @Operation(summary = "Get company details by ID",
            description = "Returns complete company information including description and images")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company found",
                    content = @Content(schema = @Schema(implementation = CompanyAllInfoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CompanyAllInfoResponseDTO> getCompanyById(
            @Parameter(description = "ID of the company to retrieve", example = "1")
            @PathVariable int id) {

        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    @Operation(summary = "Get company with posts",
            description = "Returns company information along with associated job post IDs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company found",
                    content = @Content(schema = @Schema(implementation = CompanyWithPostsResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @GetMapping("/{id}/with-posts")
    public ResponseEntity<CompanyWithPostsResponseDTO> getCompanyWithPosts(
            @Parameter(description = "ID of the company")
            @PathVariable int id) {

        return ResponseEntity.ok(companyService.getCompanyWithPostsById(id));
    }

    @Operation(summary = "Create new company",
            description = "Creates a new company with provided information")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Company created successfully",
                    content = @Content(schema = @Schema(implementation = CompanyAllInfoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Company already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Company> createCompany(
            @Valid @RequestBody
            @Parameter(description = "Company creation data", required = true)
            CompanyCreateRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.createCompany(request));
    }

    @Operation(summary = "Update company",
            description = "Updates existing company information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company updated successfully",
                    content = @Content(schema = @Schema(implementation = CompanyAllInfoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CompanyAllInfoResponseDTO> updateCompany(
            @Parameter(description = "ID of the company to update")
            @PathVariable int id,

            @Valid @RequestBody
            @Parameter(description = "Updated company data", required = true)
            CompanyUpdateRequestDTO request) {

        return ResponseEntity.ok(companyService.updateCompany(id, request));
    }

    @Operation(summary = "Delete company",
            description = "Deletes a company by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Company deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteCompany(
            @Parameter(description = "ID of the company to delete")
            @PathVariable int id) {

        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Check company existence by name",
            description = "Checks if company with given name already exists")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Existence check completed")
    })
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkCompanyExists(
            @Parameter(description = "Company name to check", required = true)
            @RequestParam String name) {

        return ResponseEntity.ok(companyService.existsByName(name));
    }
}