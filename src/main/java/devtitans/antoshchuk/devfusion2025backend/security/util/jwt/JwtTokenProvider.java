package devtitans.antoshchuk.devfusion2025backend.security.util.jwt;

import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import devtitans.antoshchuk.devfusion2025backend.models.user.Seeker;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.repositories.UserAccountRepository;
import devtitans.antoshchuk.devfusion2025backend.services.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    private final CustomUserDetailsService userService;
    private final UserAccountRepository userAccountRepository;

    @Transactional
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        
        UserAccount userAccount = userAccountRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("userId", userAccount.getId());
        claims.put("userType", userAccount.getUserType().getName());

        String fullName = "";

        if ("COMPANY".equals(userAccount.getUserType().getName())) {
            Company company = userAccount.getCompany();
            if (company != null && company.getName() != null) {
                fullName = company.getName();
            }
        } else if ("SEEKER".equals(userAccount.getUserType().getName())) {
            Seeker seeker = userAccount.getSeeker();
            if (seeker != null) {
                String firstName = seeker.getFirstName() != null ? seeker.getFirstName() : "";
                String lastName = seeker.getLastName() != null ? seeker.getLastName() : "";
                if (!firstName.isEmpty() || !lastName.isEmpty()) {
                    fullName = (firstName + " " + lastName).trim();
                }
            }
        }

        claims.put("fullName", fullName);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRole(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
            return !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            throw new RuntimeException("Неприпустимий або прострочений JWT токен");
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
