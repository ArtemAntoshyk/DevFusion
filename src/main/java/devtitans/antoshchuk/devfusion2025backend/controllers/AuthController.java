package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.request.UserLoginRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.request.UserRegisterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.AuthResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.exceptions.AuthenticationException;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.repositories.UserAccountRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
            @ApiResponse(responseCode = "200", description = "User successfully registered and authenticated"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody(description = "User registration data", required = true)
            @org.springframework.web.bind.annotation.RequestBody UserRegisterRequestDTO userRegDTO) {

        try {
            return ResponseEntity.ok(authService.register(userRegDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary = "Authenticate a user", description = "Logs in a user by verifying email and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid login credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestBody(description = "User login credentials", required = true)
            @org.springframework.web.bind.annotation.RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        
        try {
            AuthResponseDTO response = authService.authenticate(userLoginRequestDTO);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}
