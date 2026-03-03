package kz.shyngys.springbootfilestorage.security;

import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthenticationException extends AuthenticationException {
    private HttpStatus status;

    public JwtAuthenticationException(@Nullable String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }

    public JwtAuthenticationException(@Nullable String msg) {
        super(msg);
    }
}
