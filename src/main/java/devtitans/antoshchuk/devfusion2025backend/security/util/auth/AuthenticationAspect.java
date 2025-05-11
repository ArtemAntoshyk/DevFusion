package devtitans.antoshchuk.devfusion2025backend.security.util.auth;

import devtitans.antoshchuk.devfusion2025backend.security.util.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthenticationAspect {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Before("@annotation(AuthenticatedEndpoint)")
    public void authenticate(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = jwtTokenProvider.resolveToken(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("Unauthorized");
        }
    }
}