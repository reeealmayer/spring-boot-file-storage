package kz.shyngys.springbootfilestorage.rest;

import kz.shyngys.springbootfilestorage.model.UserEntity;
import kz.shyngys.springbootfilestorage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final UserRepository userRepository;


    @GetMapping("/test")
    public Mono<UserEntity> test() {
        Mono<UserEntity> byId = userRepository.findById(1L);
        return byId;
    }
}
