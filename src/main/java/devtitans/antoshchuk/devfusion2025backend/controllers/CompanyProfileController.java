package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.request.CompanyProfileUpdateRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyProfileResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.RestApiResponse;
import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import devtitans.antoshchuk.devfusion2025backend.models.user.CompanyImage;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.repositories.UserAccountRepository;
import devtitans.antoshchuk.devfusion2025backend.security.util.jwt.JwtTokenProvider;
import devtitans.antoshchuk.devfusion2025backend.services.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@Tag(
    name = "Company Profile",
    description = """
        Endpoints for managing company profiles.
        
        ## Authentication
        All endpoints require a valid JWT token in the Authorization header:
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
        
        ## Common HTTP Status Codes
        - 200: Success
        - 400: Bad Request (validation errors)
        - 401: Unauthorized (invalid/missing token)
        - 403: Forbidden (insufficient permissions)
        - 404: Not Found
        - 409: Conflict (e.g., email already exists)
        """
)
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CompanyProfileController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserAccountRepository userAccountRepository;
    private final CompanyService companyService;

    @GetMapping("/me")
    @Operation(
        summary = "Get authenticated company profile",
        description = """
            Retrieves the complete profile of the currently authenticated company.
            
            ## Response Format
            ```json
            {
                "success": true,
                "message": "Company profile retrieved successfully",
                "data": {
                    "id": 1,
                    "name": "Company Name",
                    "businessStreamName": "IT Services",
                    "companyLogo": "https://example.com/logo.png",
                    "companyDescription": "Company description",
                    "companyWebsiteUrl": "https://company.com",
                    "establishmentDate": "2020-01-01",
                    "companyImages": [
                        "https://example.com/image1.jpg",
                        "https://example.com/image2.jpg"
                    ],
                    "email": "company@example.com",
                    "contactNumber": "+380501234567"
                }
            }
            ```
            
            ## Notes
            - All fields are optional in the response
            - `establishmentDate` is in ISO-8601 format (YYYY-MM-DD)
            - `companyImages` is an array of image URLs
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved company profile",
            content = @Content(
                schema = @Schema(implementation = CompanyProfileResponseDTO.class),
                examples = @ExampleObject(
                    value = """
                        {
                            "success": true,
                            "message": "Company profile retrieved successfully",
                            "data": {
                                "id": 1,
                                "name": "Tech Solutions Ltd",
                                "businessStreamName": "IT Services",
                                "companyLogo": "https://example.com/logo.png",
                                "companyDescription": "Leading IT solutions provider",
                                "companyWebsiteUrl": "https://techsolutions.com",
                                "establishmentDate": "2020-01-01",
                                "companyImages": [
                                    "https://example.com/office1.jpg",
                                    "https://example.com/team.jpg"
                                ],
                                "email": "contact@techsolutions.com",
                                "contactNumber": "+380501234567"
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing token",
            content = @Content(
                schema = @Schema(implementation = RestApiResponse.class),
                examples = @ExampleObject(
                    value = """
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
            description = "Forbidden - User is not a company",
            content = @Content(
                schema = @Schema(implementation = RestApiResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                            "success": false,
                            "message": "Forbidden - User is not a company",
                            "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Company profile not found",
            content = @Content(
                schema = @Schema(implementation = RestApiResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                            "success": false,
                            "message": "Company profile not found",
                            "data": null
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<RestApiResponse<CompanyProfileResponseDTO>> getCompanyProfile(
            @Parameter(hidden = true) HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new RestApiResponse<>(
                    false,
                    "Unauthorized - Invalid or missing token",
                    null
                ));
        }

        String username = jwtTokenProvider.getUsername(token);
        UserAccount userAccount = userAccountRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"COMPANY".equals(userAccount.getUserType().getName())) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new RestApiResponse<>(
                    false,
                    "Forbidden - User is not a company",
                    null
                ));
        }

        Company company = userAccount.getCompany();
        if (company == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new RestApiResponse<>(
                    false,
                    "Company profile not found",
                    null
                ));
        }

        // Create response DTO with company and user information
        CompanyProfileResponseDTO companyProfile = new CompanyProfileResponseDTO(
            company.getId(),
            company.getName(),
            company.getBusinessStreamName(),
            company.getLogo(),
            company.getCompanyDescription(),
            company.getCompanyWebsiteUrl(),
            company.getEstablishmentDate(),
            company.getCompanyImages().stream()
                .map(CompanyImage::getCompany_image)
                .collect(Collectors.toList()),
            userAccount.getEmail(),
            userAccount.getContactNumber()
        );

        return ResponseEntity
            .ok(new RestApiResponse<>(
                true,
                "Company profile retrieved successfully",
                companyProfile
            ));
    }

    @PutMapping("/me")
    @Operation(
        summary = "Update company profile",
        description = """
            Updates the profile of the currently authenticated company.
            
            ## Request Format
            ```json
            {
                "name": "New Company Name",
                "businessStreamName": "IT Services",
                "companyLogo": "https://example.com/logo.png",
                "companyDescription": "Updated company description",
                "companyWebsiteUrl": "https://company.com",
                "establishmentDate": "2020-01-01",
                "companyImages": [
                    "https://example.com/image1.jpg",
                    "https://example.com/image2.jpg"
                ],
                "email": "new.email@company.com",
                "contactNumber": "+380501234567"
            }
            ```
            
            ## Field Validations
            - `name`: 2-100 characters
            - `businessStreamName`: 2-100 characters
            - `companyLogo`: max 500 characters, valid URL
            - `companyDescription`: max 2000 characters
            - `companyWebsiteUrl`: max 500 characters, valid URL format
            - `establishmentDate`: valid date in YYYY-MM-DD format
            - `companyImages`: array of valid image URLs
            - `email`: valid email format, max 100 characters
            - `contactNumber`: international format (e.g., +380501234567), 10-15 digits
            
            ## Notes
            - All fields are optional
            - Only provided fields will be updated
            - Email must be unique across all users
            - Images will replace all existing images
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully updated company profile",
            content = @Content(
                schema = @Schema(implementation = CompanyProfileResponseDTO.class),
                examples = @ExampleObject(
                    value = """
                        {
                            "success": true,
                            "message": "Company profile updated successfully",
                            "data": {
                                "id": 1,
                                "name": "New Company Name",
                                "businessStreamName": "IT Services",
                                "companyLogo": "https://example.com/logo.png",
                                "companyDescription": "Updated company description",
                                "companyWebsiteUrl": "https://company.com",
                                "establishmentDate": "2020-01-01",
                                "companyImages": [
                                    "https://example.com/image1.jpg",
                                    "https://example.com/image2.jpg"
                                ],
                                "email": "new.email@company.com",
                                "contactNumber": "+380501234567"
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(
                schema = @Schema(implementation = RestApiResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                            "success": false,
                            "message": "Invalid request data: [field] [error description]",
                            "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing token",
            content = @Content(
                schema = @Schema(implementation = RestApiResponse.class),
                examples = @ExampleObject(
                    value = """
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
            description = "Forbidden - User is not a company",
            content = @Content(
                schema = @Schema(implementation = RestApiResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                            "success": false,
                            "message": "Forbidden - User is not a company",
                            "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Company profile not found",
            content = @Content(
                schema = @Schema(implementation = RestApiResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                            "success": false,
                            "message": "Company profile not found",
                            "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Email already exists",
            content = @Content(
                schema = @Schema(implementation = RestApiResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                            "success": false,
                            "message": "Email already exists",
                            "data": null
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<RestApiResponse<CompanyProfileResponseDTO>> updateCompanyProfile(
            @Valid @RequestBody CompanyProfileUpdateRequestDTO updateRequest,
            @Parameter(hidden = true) HttpServletRequest request) {
        
        String token = jwtTokenProvider.resolveToken(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new RestApiResponse<>(
                    false,
                    "Unauthorized - Invalid or missing token",
                    null
                ));
        }

        String username = jwtTokenProvider.getUsername(token);
        UserAccount userAccount = userAccountRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"COMPANY".equals(userAccount.getUserType().getName())) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new RestApiResponse<>(
                    false,
                    "Forbidden - User is not a company",
                    null
                ));
        }

        Company company = userAccount.getCompany();
        if (company == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new RestApiResponse<>(
                    false,
                    "Company profile not found",
                    null
                ));
        }

        // Check if new email is already taken by another user
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(userAccount.getEmail())) {
            if (userAccountRepository.existsByEmail(updateRequest.getEmail())) {
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new RestApiResponse<>(
                        false,
                        "Email already exists",
                        null
                    ));
            }
        }

        // Update company information
        if (updateRequest.getName() != null) {
            company.setName(updateRequest.getName());
        }
        if (updateRequest.getBusinessStreamName() != null) {
            company.setBusinessStreamName(updateRequest.getBusinessStreamName());
        }
        if (updateRequest.getCompanyLogo() != null) {
            company.setLogo(updateRequest.getCompanyLogo());
        }
        if (updateRequest.getCompanyDescription() != null) {
            company.setCompanyDescription(updateRequest.getCompanyDescription());
        }
        if (updateRequest.getCompanyWebsiteUrl() != null) {
            company.setCompanyWebsiteUrl(updateRequest.getCompanyWebsiteUrl());
        }
        if (updateRequest.getEstablishmentDate() != null) {
            company.setEstablishmentDate(updateRequest.getEstablishmentDate());
        }

        // Update company images if provided
        if (updateRequest.getCompanyImages() != null) {
            // Clear existing images
            company.getCompanyImages().clear();
            // Add new images
            updateRequest.getCompanyImages().forEach(imageUrl -> {
                CompanyImage image = new CompanyImage(company, imageUrl);
                company.addCompanyImage(image);
            });
        }

        // Update user contact information
        if (updateRequest.getEmail() != null) {
            userAccount.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getContactNumber() != null) {
            userAccount.setContactNumber(updateRequest.getContactNumber());
        }

        // Save changes
        userAccountRepository.save(userAccount);
        companyService.save(company);

        // Create and return updated profile
        CompanyProfileResponseDTO updatedProfile = new CompanyProfileResponseDTO(
            company.getId(),
            company.getName(),
            company.getBusinessStreamName(),
            company.getLogo(),
            company.getCompanyDescription(),
            company.getCompanyWebsiteUrl(),
            company.getEstablishmentDate(),
            company.getCompanyImages().stream()
                .map(CompanyImage::getCompany_image)
                .collect(Collectors.toList()),
            userAccount.getEmail(),
            userAccount.getContactNumber()
        );

        return ResponseEntity
            .ok(new RestApiResponse<>(
                true,
                "Company profile updated successfully",
                updatedProfile
            ));
    }
} 