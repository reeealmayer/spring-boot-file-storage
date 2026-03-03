package kz.shyngys.springbootfilestorage.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter implements WebFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String token = jwtTokenProvider.resolveToken(exchange);

        if (token == null) {
            return chain.filter(exchange);
        }

        try {

            if (!jwtTokenProvider.validateToken(token)) {
                return unauthorized(exchange);
            }

            return jwtTokenProvider.getAuthentication(token)
                    .flatMap(authentication ->
                            chain.filter(exchange)
                                    .contextWrite(
                                            ReactiveSecurityContextHolder
                                                    .withAuthentication(authentication)
                                    )
                    );

        } catch (JwtAuthenticationException e) {
            return unauthorized(exchange);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}