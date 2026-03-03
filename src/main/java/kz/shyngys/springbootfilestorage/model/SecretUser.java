package kz.shyngys.springbootfilestorage.model;

import kz.shyngys.springbootfilestorage.model.enumerated.UserStatus;
import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Data
public class SecretUser implements UserDetails {
    private final String username;
    private final String password;
    private final List<SimpleGrantedAuthority> authorities;
    private final boolean isActive = true;

    public SecretUser(String username, String password, List<SimpleGrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        return authorities;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    public static UserDetails fromUser(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus().equals(UserStatus.ACTIVE),
                user.getStatus().equals(UserStatus.ACTIVE),
                user.getStatus().equals(UserStatus.ACTIVE),
                user.getStatus().equals(UserStatus.ACTIVE),
                user.getRole().getAuthorities()
        );
    }
}
