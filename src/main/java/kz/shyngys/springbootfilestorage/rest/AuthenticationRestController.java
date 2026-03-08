package kz.shyngys.springbootfilestorage.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.shyngys.springbootfilestorage.config.TokenBlacklist;
import kz.shyngys.springbootfilestorage.dto.AuthenticationRequestDTO;
import kz.shyngys.springbootfilestorage.repository.UserRepository;
import kz.shyngys.springbootfilestorage.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login / Logout")
public class AuthenticationRestController {

    private final ReactiveAuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklist tokenBlacklist;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Authenticate and get JWT token")
    public Mono<ResponseEntity<Map<String, String>>> authenticate(
            @RequestBody AuthenticationRequestDTO request
    ) {
        System.out.println(passwordEncoder.encode("test"));
        return authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()))
                .flatMap(auth -> Mono.fromCallable(() ->
                        userRepository.findByUsername(request.getUsername())
                                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exist"))
                ).subscribeOn(Schedulers.boundedElastic()))
                .map(user -> {
                    String token = jwtTokenProvider.createToken(
                            request.getUsername(), user.getRole().name());
                    return ResponseEntity.ok(Map.of(
                            "username", request.getUsername(),
                            "token", token
                    ));
                })
                .onErrorResume(ex ->
                        Mono.just(ResponseEntity.status(401)
                                .body(Map.of("error", "Invalid username or password")))
                );
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and invalidate JWT token")
    public Mono<ResponseEntity<Map<String, String>>> logout(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklist.invalidate(token);
        }
        return Mono.just(ResponseEntity.ok(Map.of("message", "Logged out successfully")));
    }
}