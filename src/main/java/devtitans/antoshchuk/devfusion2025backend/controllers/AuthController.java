package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.request.UserLoginRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.request.UserRegisterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.repositiories.UserAccountRepository;
import devtitans.antoshchuk.devfusion2025backend.security.util.jwt.JwtTokenProvider;
import devtitans.antoshchuk.devfusion2025backend.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final UserAccountRepository userAccountRepository;

    @Autowired
    public AuthController(AuthService authService,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          UserAccountRepository userAccountRepository) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userAccountRepository = userAccountRepository;
    }

    @Operation(summary = "Register a new user", description = "Registers a job seeker or a company and logs them in automatically.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered and authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"token\": \"jwt-token\", \"role\": \"USER\", \"userId\": \"1\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid registration data", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(
            @RequestBody(description = "User registration data", required = true)
            @org.springframework.web.bind.annotation.RequestBody UserRegisterRequestDTO userRegDTO) {

        UserAccount newUser;
        if (userRegDTO.getSeeker() != null) {
            newUser = authService.registerSeeker(userRegDTO);
        } else if (userRegDTO.getCompany() != null) {
            newUser = authService.registerCompany(userRegDTO);
        } else {
            throw new RuntimeException("Invalid registration data");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userRegDTO.getUser().getEmail(),
                        userRegDTO.getUser().getPassword()
                )
        );

        String token = jwtTokenProvider.generateToken(authentication);
        UserAccount userAccount = userAccountRepository.findByEmail(newUser.getEmail());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", userAccount.getUserType().getName());
        response.put("userId", String.valueOf(userAccount.getId()));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Authenticate a user", description = "Logs in a user by verifying email and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"token\": \"jwt-token\", \"role\": \"USER\", \"userId\": \"1\"}"))),
            @ApiResponse(responseCode = "401", description = "Invalid login credentials", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(
            @RequestBody(description = "User login credentials", required = true)
            @org.springframework.web.bind.annotation.RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        System.out.println("Login attempt for: " + userLoginRequestDTO.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLoginRequestDTO.getEmail(),
                            userLoginRequestDTO.getPassword()
                    )
            );
            System.out.println("Authentication successful");
            String token = jwtTokenProvider.generateToken(authentication);
            UserAccount userAccount = userAccountRepository.findByEmail(userLoginRequestDTO.getEmail());


            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("role", userAccount.getUserType().getName());
            response.put("userId", String.valueOf(userAccount.getId()));
            System.out.println(response);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            throw e;
        }
    }
}
