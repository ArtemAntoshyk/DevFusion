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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Profile", description = "Endpoints for managing user profile and retrieving user information")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UserController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserAccountRepository userAccountRepository;

    @GetMapping("/me")
    @Operation(
        summary = "Get current user profile",
        description = "Returns information about the currently authenticated user based on their JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user profile",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDataResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing token",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(example = "{\"error\":\"Unauthorized\",\"message\":\"Invalid or missing token\"}")
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(example = "{\"error\":\"Not Found\",\"message\":\"User not found\"}")
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