package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.request.UserLoginRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.request.UserRegisterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.security.util.jwt.JwtTokenProvider;
import devtitans.antoshchuk.devfusion2025backend.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private AuthService authService;
    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    // registration user with role seeker or company

    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody UserRegisterRequestDTO userRegDTO) {
        System.out.println(userRegDTO);
//        UserAccount newAcc = authService.registerUser(userRegDTO);
        if(userRegDTO.getSeeker() != null) {
            authService.registerSeeker(userRegDTO);
        } else if (userRegDTO.getCompany() != null) {
            authService.registerCompany(userRegDTO);
        }
        return ResponseEntity.ok("Successfully registered");
    }

    @PostMapping("/login")
    public ResponseEntity loginUser(@RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        System.out.println(userLoginRequestDTO);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequestDTO.getEmail(), userLoginRequestDTO.getPassword())
        );
        String token = jwtTokenProvider.generateToken(authentication);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        System.out.println(response);
        return ResponseEntity.ok(response);
    }
//@PostMapping("/login")
//public ResponseEntity loginUser(@RequestBody UserLoginRequestDTO userLoginRequestDTO) {
//
//    System.out.println(userLoginRequestDTO);
//    return ResponseEntity.ok("Successfully logged in");
//}
}
