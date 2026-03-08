package kz.shyngys.springbootfilestorage.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.shyngys.springbootfilestorage.dto.FileResponseDto;
import kz.shyngys.springbootfilestorage.service.FileService;
import kz.shyngys.springbootfilestorage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "File management API")
@SecurityRequirement(name = "bearerAuth")
public class FileController {

    private final FileService fileService;
    private final UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('files:upload')")
    @Operation(summary = "Upload file")
    public Mono<ResponseEntity<FileResponseDto>> upload(
            @RequestPart("file") FilePart filePart,
            @RequestParam("userId") Long userId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        return userService.getByUsername(principal.getUsername())
                .flatMap(currentUser -> {
                    boolean isPrivileged = principal.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                                    || a.getAuthority().equals("ROLE_MODERATOR"));
                    if (!isPrivileged && !currentUser.getId().equals(userId)) {
                        return Mono.error(new RuntimeException("Access denied: cannot upload for another user"));
                    }
                    return filePart.content()
                            .reduce(new byte[0], (acc, dataBuffer) -> {
                                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(bytes);
                                byte[] combined = new byte[acc.length + bytes.length];
                                System.arraycopy(acc, 0, combined, 0, acc.length);
                                System.arraycopy(bytes, 0, combined, acc.length, bytes.length);
                                return combined;
                            })
                            .flatMap(bytes -> {
                                String contentType = filePart.headers().getContentType() != null
                                        ? filePart.headers().getContentType().toString()
                                        : MediaType.APPLICATION_OCTET_STREAM_VALUE;
                                return fileService.upload(filePart.filename(), contentType, bytes, userId);
                            });
                })
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('files:read')")
    @Operation(summary = "Get all files (ADMIN/MODERATOR) or own files (USER)")
    public Mono<ResponseEntity<List<FileResponseDto>>> getAll(
            @AuthenticationPrincipal UserDetails principal
    ) {
        boolean isPrivileged = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_MODERATOR"));
        if (isPrivileged) {
            return fileService.getAll().map(ResponseEntity::ok);
        }
        // USER sees only files they have events for — delegate to service
        return userService.getByUsername(principal.getUsername())
                .flatMap(user -> fileService.getByUserId(user.getId()))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('files:read')")
    @Operation(summary = "Get file by ID")
    public Mono<ResponseEntity<FileResponseDto>> getById(@PathVariable Long id) {
        return fileService.getById(id).map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('files:delete')")
    @Operation(summary = "Delete file")
    public Mono<ResponseEntity<Void>> delete(
            @PathVariable Long id,
            @RequestParam("userId") Long userId,
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
                    return fileService.delete(id, userId);
                })
                .thenReturn(ResponseEntity.<Void>noContent().build());
    }
}