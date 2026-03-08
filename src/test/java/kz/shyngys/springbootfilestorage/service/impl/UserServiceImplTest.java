package kz.shyngys.springbootfilestorage.service.impl;

import kz.shyngys.springbootfilestorage.dto.UserCreateRequest;
import kz.shyngys.springbootfilestorage.dto.UserUpdateRequest;
import kz.shyngys.springbootfilestorage.model.User;
import kz.shyngys.springbootfilestorage.model.enumerated.UserRole;
import kz.shyngys.springbootfilestorage.model.enumerated.UserStatus;
import kz.shyngys.springbootfilestorage.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("alice")
                .password("$2a$10$encoded")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void create_shouldSaveNewUser() {
        UserCreateRequest req = new UserCreateRequest();
        req.setUsername("alice");
        req.setPassword("password");
        req.setRole(UserRole.USER);

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        StepVerifier.create(userService.create(req))
                .assertNext(dto -> {
                    assertThat(dto.getUsername()).isEqualTo("alice");
                    assertThat(dto.getRole()).isEqualTo(UserRole.USER);
                })
                .verifyComplete();
    }

    @Test
    void create_shouldFailIfUsernameExists() {
        UserCreateRequest req = new UserCreateRequest();
        req.setUsername("alice");
        req.setPassword("password");
        req.setRole(UserRole.USER);

        when(userRepository.existsByUsername("alice")).thenReturn(true);

        StepVerifier.create(userService.create(req))
                .expectErrorMatches(ex -> ex.getMessage().contains("already exists"))
                .verify();
    }

    @Test
    void getById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        StepVerifier.create(userService.getById(1L))
                .assertNext(dto -> assertThat(dto.getId()).isEqualTo(1L))
                .verifyComplete();
    }

    @Test
    void getAll_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        StepVerifier.create(userService.getAll())
                .assertNext(list -> assertThat(list).hasSize(1))
                .verifyComplete();
    }

    @Test
    void update_shouldChangeStatus() {
        UserUpdateRequest req = new UserUpdateRequest();
        req.setStatus(UserStatus.BLOCKED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        StepVerifier.create(userService.update(1L, req))
                .assertNext(dto -> assertThat(dto).isNotNull())
                .verifyComplete();

        verify(userRepository).save(argThat(u -> u.getStatus() == UserStatus.BLOCKED));
    }

    @Test
    void delete_shouldCallRepository() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        StepVerifier.create(userService.delete(1L))
                .verifyComplete();

        verify(userRepository).deleteById(1L);
    }
}