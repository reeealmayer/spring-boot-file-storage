package kz.shyngys.springbootfilestorage.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final ReactiveUserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long validityInSeconds;

    @Value("${jwt.header}")
    private String authorizationHeader;

    private SecretKey key;

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secretKey)
        );
    }

    public String createToken(String username, String role) {

        Date now = new Date();
        Date validity =
                new Date(now.getTime() + validityInSeconds * 1000);

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    public Mono<Authentication> getAuthentication(String token) {

        String username = getUsername(token);

        return userDetailsService.findByUsername(username)
                .map(this::createAuthentication);
    }

    private Authentication createAuthentication(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;

        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException(
                    "JWT token is expired or invalid",
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    public String getUsername(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String resolveToken(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest()
                .getHeaders()
                .getFirst(authorizationHeader);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}