package kz.shyngys.springbootfilestorage.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.shyngys.springbootfilestorage.dto.EventResponseDto;
import kz.shyngys.springbootfilestorage.service.EventService;
import kz.shyngys.springbootfilestorage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Event management API")
@SecurityRequirement(name = "bearerAuth")
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Get all events (ADMIN, MODERATOR)")
    public Mono<ResponseEntity<List<EventResponseDto>>> getAll() {
        return eventService.getAll().map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('events:read')")
    @Operation(summary = "Get event by ID")
    public Mono<ResponseEntity<EventResponseDto>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal
    ) {
        return eventService.getById(id)
                .flatMap(event -> {
                    boolean isPrivileged = principal.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                                    || a.getAuthority().equals("ROLE_MODERATOR"));
                    if (isPrivileged) {
                        return Mono.just(event);
                    }
                    return userService.getByUsername(principal.getUsername())
                            .flatMap(currentUser -> {
                                if (!currentUser.getId().equals(event.getUserId())) {
                                    return Mono.error(new RuntimeException("Access denied"));
                                }
                                return Mono.just(event);
                            });
                })
                .map(ResponseEntity::ok);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('events:read')")
    @Operation(summary = "Get events by user ID")
    public Mono<ResponseEntity<List<EventResponseDto>>> getByUserId(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        return userService.getByUsername(principal.getUsername())
                .flatMap(currentUser -> {
                    boolean isPrivileged = principal.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                                    || a.getAuthority().equals("ROLE_MODERATOR"));
                    if (!isPrivileged && !currentUser.getId().equals(userId)) {
                        return Mono.error(new RuntimeException("Access denied"));
                    }
                    return eventService.getByUserId(userId);
                })
                .map(ResponseEntity::ok);
    }

    @GetMapping("/file/{fileId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Get events by file ID (ADMIN, MODERATOR)")
    public Mono<ResponseEntity<List<EventResponseDto>>> getByFileId(@PathVariable Long fileId) {
        return eventService.getByFileId(fileId).map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('events:delete')")
    @Operation(summary = "Delete event (ADMIN, MODERATOR)")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return eventService.delete(id)
                .thenReturn(ResponseEntity.<Void>noContent().build());
    }
}