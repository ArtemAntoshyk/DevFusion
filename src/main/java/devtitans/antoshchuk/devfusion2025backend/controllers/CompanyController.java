package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyAllInfoResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyBaseResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyWithPostsResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.PaginatedCompanyResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.services.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
@Tag(
    name = "Company Management",
    description = """
        Endpoints for managing companies.
        
        ## Authentication
        Most endpoints require a valid JWT token in the Authorization header:
        ```
        Authorization: Bearer <your_jwt_token>
        ```
        
        ## Error Responses
        All endpoints return standardized error responses in the following format:
        ```json
        {
            "success": false,
            "message": "Error description",
            "data": null
        }
        ```
        """
)
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    @Operation(
        summary = "Get all companies with basic information",
        description = """
            Returns a list of all companies with their basic information.
            
            ## Response Format
            ```json
            {
                "success": true,
                "message": "Companies retrieved successfully",
                "data": [
                    {
                        "id": 1,
                        "name": "Tech Solutions",
                        "businessStreamName": "IT Services",
                        "companyLogo": "https://example.com/logo.png"
                    }
                ]
            }
            ```
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved companies",
            content = @Content(
                schema = @Schema(implementation = CompanyBaseResponseDTO.class),
                examples = @ExampleObject(
                    value = """
                        {
                            "success": true,
                            "message": "Companies retrieved successfully",
                            "data": [
                                {
                                    "id": 1,
                                    "name": "Tech Solutions",
                                    "businessStreamName": "IT Services",
                                    "companyLogo": "https://example.com/logo.png"
                                }
                            ]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<List<CompanyBaseResponseDTO>> getAllCompaniesBasic() {
        return ResponseEntity.ok(companyService.getAllCompaniesBaseInfoDTOs());
    }

    @GetMapping("/with-posts")
    @Operation(
        summary = "Get all companies with their job posts",
        description = """
            Returns a list of all companies including their job posts.
            
            ## Response Format
            ```json
            {
                "success": true,
                "message": "Companies with posts retrieved successfully",
                "data": [
                    {
                        "id": 1,
                        "name": "Tech Solutions",
                        "businessStreamName": "IT Services",
                        "companyLogo": "https://example.com/logo.png",
                        "jobPosts": [
                            {
                                "id": 1,
                                "title": "Senior Developer",
                                "description": "Job description...",
                                "location": "London, UK",
                                "createdDateTime": "2024-03-20T10:00:00"
                            }
                        ]
                    }
                ]
            }
            ```
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved companies with posts",
                    content = @Content(schema = @Schema(implementation = CompanyWithPostsResponseDTO.class)))
    })
    public ResponseEntity<List<CompanyWithPostsResponseDTO>> getAllCompaniesWithPosts() {
        return ResponseEntity.ok(companyService.getAllCompaniesWithPostsDTOs());
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get company by ID with all information",
        description = """
            Returns detailed information about a specific company.
            
            ## Response Format
            ```json
            {
                "success": true,
                "message": "Company retrieved successfully",
                "data": {
                    "id": 1,
                    "name": "Tech Solutions",
                    "businessStreamName": "IT Services",
                    "companyLogo": "https://example.com/logo.png",
                    "companyDescription": "Company description...",
                    "companyWebsiteUrl": "https://company.com",
                    "establishmentDate": "2020-01-01",
                    "companyImages": [
                        "https://example.com/image1.jpg"
                    ],
                    "email": "contact@company.com",
                    "contactNumber": "+380501234567"
                }
            }
            ```
            """
    )
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
    @Operation(
        summary = "Search and filter companies with pagination",
        description = """
            Search and filter companies with pagination support.
            
            ## Parameters
            - page: Page number (0-based)
            - size: Page size (default: 10)
            - search: Search term for company name
            - businessStream: Business stream filter
            
            ## Response Format
            ```json
            {
                "success": true,
                "message": "Companies retrieved successfully",
                "data": {
                    "content": [
                        {
                            "id": 1,
                            "name": "Tech Solutions",
                            "businessStreamName": "IT Services",
                            "companyLogo": "https://example.com/logo.png"
                        }
                    ],
                    "pageable": {
                        "pageNumber": 0,
                        "pageSize": 10,
                        "sort": {
                            "sorted": false
                        }
                    },
                    "totalElements": 100,
                    "totalPages": 10,
                    "last": false,
                    "first": true,
                    "empty": false
                }
            }
            ```
            """
    )
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
    @Operation(
        summary = "Delete a company",
        description = """
            Deletes a company by its ID.
            
            ## Notes
            - Requires authentication
            - Only company owners can delete their own company
            - This action cannot be undone
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Company successfully deleted"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing token",
            content = @Content(
                schema = @Schema(
                    example = """
                        {
                            "success": false,
                            "message": "Unauthorized - Invalid or missing token",
                            "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Not authorized to delete this company",
            content = @Content(
                schema = @Schema(
                    example = """
                        {
                            "success": false,
                            "message": "Forbidden - Not authorized to delete this company",
                            "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Company not found",
            content = @Content(
                schema = @Schema(
                    example = """
                        {
                            "success": false,
                            "message": "Company not found",
                            "data": null
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<Void> deleteCompany(
            @Parameter(description = "Company ID") @PathVariable int id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
} 