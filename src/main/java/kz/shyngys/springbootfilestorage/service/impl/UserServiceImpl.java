package kz.shyngys.springbootfilestorage.service.impl;

import kz.shyngys.springbootfilestorage.dto.UserCreateRequest;
import kz.shyngys.springbootfilestorage.dto.UserResponse;
import kz.shyngys.springbootfilestorage.dto.UserUpdateRequest;
import kz.shyngys.springbootfilestorage.model.User;
import kz.shyngys.springbootfilestorage.model.enumerated.UserStatus;
import kz.shyngys.springbootfilestorage.repository.UserRepository;
import kz.shyngys.springbootfilestorage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserResponse> create(UserCreateRequest request) {
        return Mono.fromCallable(() -> {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username already exists: " + request.getUsername());
            }
            User user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .status(UserStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            return toDto(userRepository.save(user));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UserResponse> getById(Long id) {
        return Mono.fromCallable(() ->
                userRepository.findById(id)
                        .map(this::toDto)
                        .orElseThrow(() -> new RuntimeException("User not found: " + id))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<List<UserResponse>> getAll() {
        return Mono.fromCallable(() ->
                userRepository.findAll().stream().map(this::toDto).toList()
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UserResponse> update(Long id, UserUpdateRequest request) {
        return Mono.fromCallable(() -> {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found: " + id));
            if (request.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            if (request.getRole() != null) {
                user.setRole(request.getRole());
            }
            if (request.getStatus() != null) {
                user.setStatus(request.getStatus());
            }
            user.setUpdatedAt(LocalDateTime.now());
            return toDto(userRepository.save(user));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> delete(Long id) {
        return Mono.fromRunnable(() -> {
            if (!userRepository.existsById(id)) {
                throw new RuntimeException("User not found: " + id);
            }
            userRepository.deleteById(id);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<UserResponse> getByUsername(String username) {
        return Mono.fromCallable(() ->
                userRepository.findByUsername(username)
                        .map(this::toDto)
                        .orElseThrow(() -> new RuntimeException("User not found: " + username))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    private UserResponse toDto(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole(), user.getStatus());
    }
}
