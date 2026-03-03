package kz.shyngys.springbootfilestorage.rest;

import kz.shyngys.springbootfilestorage.dto.UserResponse;
import kz.shyngys.springbootfilestorage.model.User;
import kz.shyngys.springbootfilestorage.repository.UserRepository;
import kz.shyngys.springbootfilestorage.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final UserRepository userRepository;
    private final S3Service s3Service;


    @GetMapping("/test")
    @PreAuthorize("hasAuthority('users:read')")
    public Mono<UserResponse> test() {

        return Mono.fromCallable(() -> {

            User entity = userRepository.findById(1L)
                    .orElseThrow();

            return new UserResponse(
                    entity.getId(),
                    entity.getUsername(),
                    entity.getRole(),
                    entity.getStatus()
            );

        }).subscribeOn(Schedulers.boundedElastic());
    }
}
