package devtitans.antoshchuk.devfusion2025backend.security.util.jwt;

import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import devtitans.antoshchuk.devfusion2025backend.models.user.Seeker;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.repositiories.UserAccountRepository;
import devtitans.antoshchuk.devfusion2025backend.services.CustomUserDetailsService;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.UserTypes;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Autowired
    private CustomUserDetailsService userService;
    @Autowired
    private UserAccountRepository userAccountRepository;

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        UserAccount userAccount = userAccountRepository.findByEmail(username);

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("userId", userAccount.getId());
        claims.put("userType", userAccount.getUserType().getName());

        if(userAccount.getUserType().getName().equals("COMPANY")) {
            Company company = userAccount.getCompany();
            claims.put("fullName", company.getName());
        }
        else if(userAccount.getUserType().getName().equals("SEEKER")) {
            Seeker seeker = userAccount.getSeeker();
            claims.put("fullName", seeker.getFirstName() + " " + seeker.getLastName());
        }

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
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
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
