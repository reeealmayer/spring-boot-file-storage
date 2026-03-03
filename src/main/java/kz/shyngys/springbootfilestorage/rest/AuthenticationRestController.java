package kz.shyngys.springbootfilestorage.rest;

import kz.shyngys.springbootfilestorage.dto.AuthenticationRequestDTO;
import kz.shyngys.springbootfilestorage.repository.UserRepository;
import kz.shyngys.springbootfilestorage.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {

    private final ReactiveAuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public Mono<?> authenticate(@RequestBody AuthenticationRequestDTO request) {

        Mono<Map<String, String>> result = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                )
                .flatMap(auth -> Mono.fromCallable(() ->
                                        userRepository.findByUsername(request.getUsername())
                                                .orElseThrow(() ->
                                                        new UsernameNotFoundException("User doesn't exist")
                                                )
                                )
                                .subscribeOn(Schedulers.boundedElastic())
                )
                .map(user -> {

                    String token = jwtTokenProvider.createToken(
                            request.getUsername(),
                            user.getRole().name()
                    );

                    return Map.of(
                            "username", request.getUsername(),
                            "token", token
                    );
                })
                .onErrorResume(ex ->
                        Mono.error(new RuntimeException("Invalid username/password"))
                );
        return result;
    }

    @PostMapping("/logout")
    public Mono<Void> logout() {
        //TODO доделать
        return Mono.empty();
    }
}