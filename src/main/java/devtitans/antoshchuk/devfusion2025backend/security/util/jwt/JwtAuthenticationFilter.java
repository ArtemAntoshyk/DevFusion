package devtitans.antoshchuk.devfusion2025backend.security.util.jwt;

import devtitans.antoshchuk.devfusion2025backend.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();
        boolean isPublic =
            path.equals("/api/auth/register") ||
            path.equals("/api/auth/login") ||
            (path.startsWith("/api/v1/companies")) ||
            (path.startsWith("/api/v1/job-posts")) ||
            (path.startsWith("/api/v1/statistics/companies")) ||
            path.startsWith("/api/v1/recommend");

        String token = jwtTokenProvider.resolveToken(request);
        System.out.println("[JwtAuthenticationFilter] path=" + path + ", isPublic=" + isPublic + ", token=" + (token != null));
        if (token != null) {
            try {
                if (jwtTokenProvider.validateToken(token)) {
                    String username = jwtTokenProvider.getUsername(token);
                    UserDetails userDetails = userDetailsService.loadUserByEmail(username);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("[JwtAuthenticationFilter] Authenticated user: " + username);
                }
            } catch (RuntimeException ex) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                System.out.println("[JwtAuthenticationFilter] Invalid or expired token");
                return;
            }
        } else if (!isPublic) {
            // Если приватный эндпоинт и нет токена — 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing token");
            System.out.println("[JwtAuthenticationFilter] Missing token for private endpoint");
            return;
        }

        // Передаю оригинальный request дальше по цепочке фильтров, чтобы тело запроса не терялось
        filterChain.doFilter(request, response);
    }
}


