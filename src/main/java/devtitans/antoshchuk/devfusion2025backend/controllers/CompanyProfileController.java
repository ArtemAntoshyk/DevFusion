package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.request.CompanyProfileUpdateRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.ApiResponse;
import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyProfileResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.security.detail.CustomUserDetails;
import devtitans.antoshchuk.devfusion2025backend.services.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/company/profile")
@Tag(
    name = "Company Profile",
    description = "Endpoints for managing authenticated company profile."
)
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CompanyProfileController {

    private final CompanyService companyService;

    @Operation(
        summary = "Get authenticated company's profile",
        description = "Returns the profile information of the currently authenticated company.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved company profile",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CompanyProfileResponseDTO.class),
                    examples = @ExampleObject(
                        value = """
                        {
                            \"success\": true,
                            \"message\": \"Company profile retrieved successfully\",
                            \"data\": {
                                \"id\": 1,
                                \"name\": \"Tech Solutions\",
                                \"businessStreamName\": \"IT Services\",
                                \"companyLogo\": \"https://example.com/logo.png\",
                                \"companyDescription\": \"Company description...\",
                                \"companyWebsiteUrl\": \"https://company.com\",
                                \"establishmentDate\": \"2020-01-01\",
                                \"companyImages\": [
                                    \"https://example.com/image1.jpg\"
                                ],
                                \"email\": \"contact@company.com\",
                                \"contactNumber\": "+380501234567"
                            }
                        }
                        """
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing token",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = """
                        {
                            \"success\": false,
                            \"message\": \"Unauthorized - Invalid or missing token\",
                            \"data\": null
                        }
                        """
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Company profile not found",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = """
                        {
                            \"success\": false,
                            \"message\": \"Company profile not found\",
                            \"data\": null
                        }
                        """
                    )
                )
            )
        }
    )
    @GetMapping
    public ResponseEntity<?> getCompanyProfile(Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UserAccount userAccount = userDetails.getUser();
            CompanyProfileResponseDTO profile = companyService.getCompanyProfile(userAccount.getId());
            if (profile == null) {
                return ResponseEntity.status(404).body(new ApiResponse<>(false, "Company profile not found", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Company profile retrieved successfully", profile));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>(false, "Error retrieving company profile: " + e.getMessage(), null));
        }
    }

    @Operation(
        summary = "Update authenticated company's profile",
        description = "Updates the profile information of the currently authenticated company.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CompanyProfileUpdateRequestDTO.class),
                examples = @ExampleObject(
                    value = """
                    {
                        \"name\": \"Tech Solutions\",
                        \"businessStreamName\": \"IT Services\",
                        \"companyLogo\": \"https://example.com/logo.png\",
                        \"companyDescription\": \"Company description...\",
                        \"companyWebsiteUrl\": \"https://company.com\",
                        \"establishmentDate\": \"2020-01-01\",
                        \"companyImages\": [\"https://example.com/image1.jpg\"],
                        \"email\": \"contact@company.com\",
                        \"contactNumber\": "+380501234567"
                    }
                    """
                )
            )
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successfully updated company profile",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CompanyProfileResponseDTO.class),
                    examples = @ExampleObject(
                        value = """
                        {
                            \"success\": true,
                            \"message\": \"Company profile updated successfully\",
                            \"data\": {
                                \"id\": 1,
                                \"name\": \"Tech Solutions\",
                                \"businessStreamName\": \"IT Services\",
                                \"companyLogo\": \"https://example.com/logo.png\",
                                \"companyDescription\": \"Company description...\",
                                \"companyWebsiteUrl\": \"https://company.com\",
                                \"establishmentDate\": \"2020-01-01\",
                                \"companyImages\": [
                                    \"https://example.com/image1.jpg\"
                                ],
                                \"email\": \"contact@company.com\",
                                \"contactNumber\": "+380501234567"
                            }
                        }
                        """
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid request data",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = """
                        {
                            \"success\": false,
                            \"message\": \"Invalid request data: [field] [error description]\",
                            \"data\": null
                        }
                        """
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing token",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = """
                        {
                            \"success\": false,
                            \"message\": \"Unauthorized - Invalid or missing token\",
                            \"data\": null
                        }
                        """
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Company profile not found",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = """
                        {
                            \"success\": false,
                            \"message\": \"Company profile not found\",
                            \"data\": null
                        }
                        """
                    )
                )
            )
        }
    )
    @PutMapping
    public ResponseEntity<?> updateCompanyProfile(
            @Valid @RequestBody CompanyProfileUpdateRequestDTO requestDTO,
            Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UserAccount userAccount = userDetails.getUser();
            CompanyProfileResponseDTO profile = companyService.updateCompanyProfile(userAccount.getId(), requestDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Company profile updated successfully", profile));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>(false, "Error updating company profile: " + e.getMessage(), null));
        }
    }
} 