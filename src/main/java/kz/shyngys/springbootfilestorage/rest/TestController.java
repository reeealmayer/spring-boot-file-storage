package kz.shyngys.springbootfilestorage.rest;

import kz.shyngys.springbootfilestorage.dto.UserResponse;
import kz.shyngys.springbootfilestorage.model.UserEntity;
import kz.shyngys.springbootfilestorage.repository.UserRepository;
import kz.shyngys.springbootfilestorage.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final UserRepository userRepository;
    private final S3Service s3Service;


    @GetMapping("/test")
    public ResponseEntity<UserResponse> test() {
        Optional<UserEntity> byId1 = userRepository.findById(1L);
        UserEntity entity = byId1.get();
        UserResponse response = new UserResponse(
                entity.getId(),
                entity.getUsername(),
                entity.getRole(),
                entity.getStatus()
        );
        return ResponseEntity.ok(response);
    }
}
