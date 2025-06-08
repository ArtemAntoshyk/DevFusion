package devtitans.antoshchuk.devfusion2025backend.security.detail;

import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Slf4j
public class CustomUserDetails implements UserDetails {

    @Getter
    private final UserAccount user;

    public CustomUserDetails(UserAccount user) {
        log.info("Creating CustomUserDetails for user: {}", user);
        this.user = user;
    }

    public int getId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = user.getUserType() != null ? user.getUserType().getName() : null;
        if (role != null) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return List.of();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
