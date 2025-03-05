package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.request.UserRegisterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.request.UserSeekerRegisterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.services.AuthService;
import devtitans.antoshchuk.devfusion2025backend.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    // registration user with role seeker or company

    @PostMapping("/registration")
    public ResponseEntity registerUser(@RequestBody UserSeekerRegisterRequestDTO userRegDTO) {
        System.out.println(userRegDTO);
        UserAccount newAcc = authService.registerUser(userRegDTO);
        if(newAcc != null) {
            return ResponseEntity.ok("Successfully registered");
        }
        else {
            return ResponseEntity.ok("Failed to register");
        }
    }

//    @PostMapping("/registration/company")
//    public ResponseEntity registerUser(@RequestBody UserRegisterRequestDTO userRegDTO) {
//        System.out.println(userRegDTO);
//        UserAccount newAcc = authService.registerUser(userRegDTO);
//        if(newAcc != null) {
//            return ResponseEntity.ok("Successfully registered");
//        }
//        else {
//            return ResponseEntity.ok("Failed to register");
//        }
//    }
    // login user

    //logout user

    //change credentials
}
