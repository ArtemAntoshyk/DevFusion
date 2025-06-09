package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.request.SeekerProfileRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.ApiResponse;
import devtitans.antoshchuk.devfusion2025backend.dto.response.SeekerProfileResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.security.detail.CustomUserDetails;
import devtitans.antoshchuk.devfusion2025backend.services.SeekerProfileService;
import devtitans.antoshchuk.devfusion2025backend.services.SeekerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/seeker/profile")
@Tag(
    name = "Seeker Profile",
    description = """
        Endpoints for managing seeker profiles.
        
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
        """
)
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class SeekerProfileController {

    private final SeekerProfileService seekerProfileService;
    private final SeekerService seekerService;

    @GetMapping
    @Operation(
        summary = "Get authenticated seeker's profile",
        description = "Returns the profile information of the currently authenticated seeker.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved seeker profile",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SeekerProfileResponseDTO.class),
                    examples = @ExampleObject(
                        value = """
                        {
                            "success": true,
                            "message": "Seeker profile retrieved successfully",
                            "data": {
                                "id": 1,
                                "firstName": "John",
                                "lastName": "Doe",
                                "dateOfBirth": "1990-01-01",
                                "currentMonthlySalary": 5000.0,
                                "cvUrl": "https://example.com/cv.pdf",
                                "seekerTitle": "Senior Java Developer",
                                "email": "john.doe@example.com",
                                "contactNumber": "+380501234567",
                                "skills": [
                                    {"skillName": "Java", "proficiencyLevel": "EXPERT"}
                                ],
                                "education": [
                                    {"degree": "Bachelor's", "major": "Computer Science", "institution": "University of Kyiv", "startDate": "2010-09-01", "endDate": "2014-06-30"}
                                ],
                                "experience": [
                                    {"jobTitle": "Senior Java Developer", "companyName": "Tech Solutions", "startDate": "2018-01-01", "endDate": "2023-12-31", "description": "Led development of enterprise applications"}
                                ],
                                "registrationDate": "2024-01-01T00:00:00"
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
                            "success": false,
                            "message": "Unauthorized - Invalid or missing token",
                            "data": null
                        }
                        """
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Seeker profile not found",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = """
                        {
                            "success": false,
                            "message": "Seeker profile not found",
                            "data": null
                        }
                        """
                    )
                )
            )
        }
    )
    public ResponseEntity<?> getSeekerProfile(Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UserAccount userAccount = userDetails.getUser();
            SeekerProfileResponseDTO profile = seekerProfileService.getSeekerProfile(userAccount.getId());
            if (profile == null) {
                return ResponseEntity.status(404).body(new ApiResponse<>(false, "Seeker profile not found", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Seeker profile retrieved successfully", profile));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>(false, "Error retrieving seeker profile: " + e.getMessage(), null));
        }
    }

    @PutMapping
    @Operation(
        summary = "Update authenticated seeker's profile",
        description = "Updates the profile information of the currently authenticated seeker, including all possible fields: personal info, skills, education, and experience.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SeekerProfileRequestDTO.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "firstName": "John",
                        "lastName": "Doe",
                        "dateOfBirth": "1990-01-01",
                        "currentMonthlySalary": 5000.0,
                        "cvUrl": "https://example.com/cv.pdf",
                        "seekerTitle": "Senior Java Developer",
                        "email": "john.doe@example.com",
                        "contactNumber": "+380501234567",
                        "skills": [
                            { "skillId": 1, "skillLevel": 5, "description": "Java expert" },
                            { "skillId": 2, "skillLevel": 4, "description": "Spring Boot" }
                        ],
                        "education": [
                            {
                                "certificateDegreeId": 1,
                                "major": "Computer Science",
                                "instituteOrUniversityName": "University of Kyiv",
                                "startDate": "2010-09-01",
                                "completionDate": "2014-06-30",
                                "cgpa": 90
                            }
                        ],
                        "experience": [
                            {
                                "isCurrentJob": true,
                                "startDate": "2018-01-01",
                                "endDate": "2023-12-31",
                                "jobTitle": "Senior Java Developer",
                                "companyName": "Tech Solutions",
                                "jobLocationCity": "Kyiv",
                                "jobLocationCountry": "Ukraine",
                                "description": "Led development of enterprise applications"
                            }
                        ]
                    }
                    """
                )
            )
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successfully updated seeker profile",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SeekerProfileResponseDTO.class),
                    examples = @ExampleObject(
                        value = """
                        {
                            "success": true,
                            "message": "Seeker profile updated successfully",
                            "data": {
                                "id": 1,
                                "firstName": "John",
                                "lastName": "Doe",
                                "dateOfBirth": "1990-01-01",
                                "currentMonthlySalary": 5000.0,
                                "cvUrl": "https://example.com/cv.pdf",
                                "seekerTitle": "Senior Java Developer",
                                "email": "john.doe@example.com",
                                "contactNumber": "+380501234567",
                                "skills": [
                                    {"skillName": "Java", "proficiencyLevel": "EXPERT"}
                                ],
                                "education": [
                                    {"degree": "Bachelor's", "major": "Computer Science", "institution": "University of Kyiv", "startDate": "2010-09-01", "endDate": "2014-06-30"}
                                ],
                                "experience": [
                                    {"jobTitle": "Senior Java Developer", "companyName": "Tech Solutions", "startDate": "2018-01-01", "endDate": "2023-12-31", "description": "Led development of enterprise applications"}
                                ],
                                "registrationDate": "2024-01-01T00:00:00"
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
                            "success": false,
                            "message": "Invalid request data: [field] [error description]",
                            "data": null
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
                            "success": false,
                            "message": "Unauthorized - Invalid or missing token",
                            "data": null
                        }
                        """
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Seeker profile not found",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = """
                        {
                            "success": false,
                            "message": "Seeker profile not found",
                            "data": null
                        }
                        """
                    )
                )
            )
        }
    )
    public ResponseEntity<?> updateSeekerProfile(
            @Valid @RequestBody SeekerProfileRequestDTO requestDTO,
            Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UserAccount userAccount = userDetails.getUser();
            SeekerProfileResponseDTO profile = seekerProfileService.updateSeekerProfile(userAccount.getId(), requestDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Seeker profile updated successfully", profile));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>(false, "Error updating seeker profile: " + e.getMessage(), null));
        }
    }

    @PostMapping(value = "/cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload seeker's CV (PDF)",
        description = "Uploads a PDF CV to Amazon S3 and updates the seeker's profile with the file link.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                schema = @Schema(type = "string", format = "binary", description = "PDF file to upload")
            )
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "CV uploaded successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = """
                        {
                          \"success\": true,
                          \"message\": \"CV uploaded successfully\",
                          \"data\": { \"cvUrl\": \"https://s3.amazonaws.com/bucket/cv/1/uuid.pdf\" }
                        }
                        """
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid file or request",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = """
                        { \"success\": false, \"message\": \"Only PDF files are allowed\", \"data\": null }
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
                        { \"success\": false, \"message\": \"Unauthorized - Invalid or missing token\", \"data\": null }
                        """
                    )
                )
            )
        }
    )
    public ResponseEntity<?> uploadCv(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UserAccount userAccount = userDetails.getUser();
            String url = seekerProfileService.uploadSeekerCv(userAccount.getId(), file);
            return ResponseEntity.ok(new ApiResponse<>(true, "CV uploaded successfully", java.util.Map.of("cvUrl", url)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ApiResponse<>(false, "Error uploading CV: " + e.getMessage(), null));
        }
    }

    @GetMapping("/api/v1/seekers")
    @Operation(
        summary = "Get paginated list of seekers with optional search and skill filters",
        description = "Returns a paginated list of seekers. Supports search by query (name, surname, title) and filtering by skill IDs.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "page", description = "Page number (zero-based)", example = "0"),
            @io.swagger.v3.oas.annotations.Parameter(name = "size", description = "Page size", example = "6"),
            @io.swagger.v3.oas.annotations.Parameter(name = "query", description = "Search query (name, surname, title)", example = "java"),
            @io.swagger.v3.oas.annotations.Parameter(name = "skillIds", description = "Comma-separated skill IDs", example = "1,2,3")
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successfully returned paginated seekers list",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SeekerProfileResponseDTO.class, type = "array"),
                    examples = @ExampleObject(
                        value = """
                        {
                          "content": [
                            {
                              "id": 1,
                              "firstName": "John",
                              "lastName": "Doe",
                              "dateOfBirth": "1990-01-01",
                              "currentMonthlySalary": 5000.0,
                              "cvUrl": "https://example.com/cv.pdf",
                              "seekerTitle": "Senior Java Developer",
                              "email": "john.doe@example.com",
                              "contactNumber": "+380501234567",
                              "skills": [
                                {"skillId": 1, "skillLevel": 5, "description": "Java expert"}
                              ],
                              "education": [
                                {"certificateDegreeId": 1, "major": "Computer Science", "instituteOrUniversityName": "University of Kyiv", "startDate": "2010-09-01", "completionDate": "2014-06-30", "cgpa": 90}
                              ],
                              "experience": [
                                {"isCurrentJob": true, "startDate": "2018-01-01", "endDate": "2023-12-31", "jobTitle": "Senior Java Developer", "companyName": "Tech Solutions", "jobLocationCity": "Kyiv", "jobLocationCountry": "Ukraine", "description": "Led development of enterprise applications"}
                              ],
                              "registrationDate": "2024-01-01T00:00:00"
                            }
                          ],
                          "pageable": {"pageNumber": 0, "pageSize": 6},
                          "totalElements": 1,
                          "totalPages": 1
                        }
                        """
                    )
                )
            )
        }
    )
    public ResponseEntity<Page<SeekerProfileResponseDTO>> getSeekers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String skillIds
    ) {
        List<Integer> skillIdList = (skillIds == null || skillIds.isBlank()) ? List.of() :
                Arrays.stream(skillIds.split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(Integer::parseInt).collect(Collectors.toList());
        Pageable pageable = PageRequest.of(page, size);
        Page<SeekerProfileResponseDTO> result = seekerService.searchSeekers(query, skillIdList, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/v1/seekers/{id}")
    @Operation(
        summary = "Get full seeker profile by id",
        description = "Returns the full profile of a seeker by their id.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "id", description = "Seeker id", required = true, example = "1")
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successfully returned seeker profile",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SeekerProfileResponseDTO.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          "success": true,
                          "message": "Seeker profile retrieved successfully",
                          "data": {
                            "id": 1,
                            "firstName": "John",
                            "lastName": "Doe",
                            "dateOfBirth": "1990-01-01",
                            "currentMonthlySalary": 5000.0,
                            "cvUrl": "https://example.com/cv.pdf",
                            "seekerTitle": "Senior Java Developer",
                            "email": "john.doe@example.com",
                            "contactNumber": "+380501234567",
                            "skills": [
                              {"skillId": 1, "skillLevel": 5, "description": "Java expert"}
                            ],
                            "education": [
                              {"certificateDegreeId": 1, "major": "Computer Science", "instituteOrUniversityName": "University of Kyiv", "startDate": "2010-09-01", "completionDate": "2014-06-30", "cgpa": 90}
                            ],
                            "experience": [
                              {"isCurrentJob": true, "startDate": "2018-01-01", "endDate": "2023-12-31", "jobTitle": "Senior Java Developer", "companyName": "Tech Solutions", "jobLocationCity": "Kyiv", "jobLocationCountry": "Ukraine", "description": "Led development of enterprise applications"}
                            ],
                            "registrationDate": "2024-01-01T00:00:00"
                          }
                        }
                        """
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Seeker not found",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = """
                        {
                          "success": false,
                          "message": "Seeker not found",
                          "data": null
                        }
                        """
                    )
                )
            )
        }
    )
    public ResponseEntity<?> getSeekerProfileById(@PathVariable Integer id) {
        var seekerOpt = seekerService.getSeekerById(id);
        if (seekerOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, "Seeker not found", null));
        }
        var seeker = seekerOpt.get();
        var dto = seekerService.getSeekerProfileResponseDTO(seeker);
        return ResponseEntity.ok(new ApiResponse<>(true, "Seeker profile retrieved successfully", dto));
    }
} 