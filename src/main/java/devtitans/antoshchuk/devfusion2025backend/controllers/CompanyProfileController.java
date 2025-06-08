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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Update authenticated company's profile",
        description = "Updates the profile information of the currently authenticated company.\n\n**Request:**\n- Method: PUT\n- URL: /api/v1/company/profile\n- Content-Type: multipart/form-data\n- Fields:\n    - name: string\n    - businessStreamName: string\n    - companyLogo: file (image, optional)\n    - companyDescription: string\n    - companyWebsiteUrl: string\n    - establishmentDate: string (yyyy-MM-dd)\n    - companyImages: file (multiple, optional)\n    - email: string\n    - contactNumber: string\n\nIf companyLogo or companyImages are not provided, old images remain. If provided, new files are uploaded to S3 and links are saved in DB. Requires Bearer JWT token.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                schema = @Schema(type = "object", implementation = CompanyProfileUpdateRequestDTO.class),
                examples = @ExampleObject(
                    value = """
                    {
                      \"name\": \"Updated Company Name\",
                      \"businessStreamName\": \"Updated Business Stream\",
                      \"companyLogo\": "(file)",
                      \"companyDescription\": \"Updated company description\",
                      \"companyWebsiteUrl\": \"https://updated-website.com\",
                      \"establishmentDate\": \"2020-01-01\",
                      \"companyImages\": "(multiple files)",
                      \"email\": \"updated@company.com\",
                      \"contactNumber\": \"+380501234567\"
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
                            \"name\": \"Updated Company Name\",
                            \"businessStreamName\": \"Updated Business Stream\",
                            \"companyLogo\": \"https://s3.amazonaws.com/bucket/company/logo/1/uuid_logo.png\",
                            \"companyDescription\": \"Updated company description\",
                            \"companyWebsiteUrl\": \"https://updated-website.com\",
                            \"establishmentDate\": \"2020-01-01\",
                            \"companyImages\": [
                              \"https://s3.amazonaws.com/bucket/company/images/1/uuid_img1.png\",
                              \"https://s3.amazonaws.com/bucket/company/images/1/uuid_img2.png\"
                            ],
                            \"email\": \"updated@company.com\",
                            \"contactNumber\": \"+380501234567\"
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
                        example = "{\"success\": false, \"message\": \"Invalid request data\", \"data\": null}"
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing token",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{\"success\": false, \"message\": \"Unauthorized - Invalid or missing token\", \"data\": null}"
                    )
                )
            )
        }
    )
    public ResponseEntity<?> updateCompanyProfile(
            @ModelAttribute CompanyProfileUpdateRequestDTO requestDTO,
            Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UserAccount userAccount = userDetails.getUser();
            CompanyProfileResponseDTO profile = companyService.updateCompanyProfile(userAccount.getId(), requestDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Company profile updated successfully", profile));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ApiResponse<>(false, "Error updating company profile: " + e.getMessage(), null));
        }
    }
} 