package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.response.UserDataResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.models.user.Seeker;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.repositiories.UserAccountRepository;
import devtitans.antoshchuk.devfusion2025backend.security.util.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @GetMapping("/me")
    public ResponseEntity<UserDataResponseDTO> getUserData(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getUsername(token);
            UserAccount userAccount = userAccountRepository.findByEmail(email);

            UserDataResponseDTO response = new UserDataResponseDTO();
            response.setEmail(userAccount.getEmail());
            response.setContactNumber(userAccount.getContactNumber());
            response.setUserType(userAccount.getUserType().getName());

            if(userAccount.getUserType().getName().equals("COMPANY")) {
                response.setFullName(userAccount.getCompany().getName());
            } else {
                Seeker seeker = userAccount.getSeeker();
                response.setFullName(seeker.getFirstName() + " " + seeker.getLastName());
            }

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}