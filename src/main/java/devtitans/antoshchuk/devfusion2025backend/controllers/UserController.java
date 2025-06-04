package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.response.UserDataResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.exceptions.ResourceNotFoundException;
import devtitans.antoshchuk.devfusion2025backend.models.user.Seeker;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.repositories.UserAccountRepository;
import devtitans.antoshchuk.devfusion2025backend.security.util.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@Tag(
    name = "User Profile",
    description = """
        Endpoints for managing user profile and retrieving user information.
        
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
public class UserController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserAccountRepository userAccountRepository;

    @GetMapping("/me")
    @Operation(
        summary = "Get current user profile",
        description = """
            Returns information about the currently authenticated user based on their JWT token.
            
            ## Response Format
            ```json
            {
                "success": true,
                "message": "User profile retrieved successfully",
                "data": {
                    "id": 1,
                    "email": "user@example.com",
                    "contactNumber": "+380501234567",
                    "userType": "SEEKER",
                    "fullName": "John Doe",
                    "userImage": "https://example.com/profile.jpg",
                    "active": true,
                    "emailNotificationActive": true,
                    "registrationDate": "2024-01-01T00:00:00"
                }
            }
            ```
            
            ## Notes
            - The response includes all user information except the password
            - For seekers, additional profile information is included
            - For companies, company profile information is included
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user profile",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDataResponseDTO.class),
                examples = @ExampleObject(
                    value = """
                        {
                            "success": true,
                            "message": "User profile retrieved successfully",
                            "data": {
                                "id": 1,
                                "email": "user@example.com",
                                "contactNumber": "+380501234567",
                                "userType": "SEEKER",
                                "fullName": "John Doe",
                                "userImage": "https://example.com/profile.jpg",
                                "active": true,
                                "emailNotificationActive": true,
                                "registrationDate": "2024-01-01T00:00:00"
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
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                            "success": false,
                            "message": "User not found",
                            "data": null
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<UserDataResponseDTO> getUserData(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(null);
        }

        String email = jwtTokenProvider.getUsername(token);
        UserAccount userAccount = userAccountRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserDataResponseDTO response = new UserDataResponseDTO();
        response.setEmail(userAccount.getEmail());
        response.setContactNumber(userAccount.getContactNumber());
        response.setUserType(userAccount.getUserType().getName());

        if (userAccount.getUserType().getName().equals("COMPANY")) {
            response.setFullName(userAccount.getCompany().getName());
        } else {
            Seeker seeker = userAccount.getSeeker();
            response.setFullName(seeker.getFirstName() + " " + seeker.getLastName());
        }

        return ResponseEntity.ok(response);
    }
}