package kz.shyngys.springbootfilestorage.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.shyngys.springbootfilestorage.dto.UserCreateRequest;
import kz.shyngys.springbootfilestorage.dto.UserResponse;
import kz.shyngys.springbootfilestorage.dto.UserUpdateRequest;
import kz.shyngys.springbootfilestorage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management API")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create user (ADMIN only)")
    public Mono<ResponseEntity<UserResponse>> create(@Valid @RequestBody UserCreateRequest request) {
        return userService.create(request)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('users:read') and (hasRole('ADMIN') or hasRole('MODERATOR'))")
    @Operation(summary = "Get all users (ADMIN, MODERATOR)")
    public Mono<ResponseEntity<List<UserResponse>>> getAll() {
        return userService.getAll().map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('users:read')")
    @Operation(summary = "Get user by ID")
    public Mono<ResponseEntity<UserResponse>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal
    ) {
        return userService.getByUsername(principal.getUsername())
                .flatMap(currentUser -> {
                    boolean isAdminOrModerator = principal.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                                    || a.getAuthority().equals("ROLE_MODERATOR"));
                    if (!isAdminOrModerator && !currentUser.getId().equals(id)) {
                        return Mono.error(new RuntimeException("Access denied"));
                    }
                    return userService.getById(id);
                })
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('users:write') or hasRole('USER')")
    @Operation(summary = "Update user")
    public Mono<ResponseEntity<UserResponse>> update(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal UserDetails principal
    ) {
        return userService.getByUsername(principal.getUsername())
                .flatMap(currentUser -> {
                    boolean isAdmin = principal.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    if (!isAdmin && !currentUser.getId().equals(id)) {
                        return Mono.error(new RuntimeException("Access denied"));
                    }
                    if (!isAdmin) {
                        request.setRole(null);
                    }
                    return userService.update(id, request);
                })
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user (ADMIN only)")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return userService.delete(id)
                .thenReturn(ResponseEntity.<Void>noContent().build());
    }
}