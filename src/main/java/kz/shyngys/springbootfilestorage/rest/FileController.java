package kz.shyngys.springbootfilestorage.rest;

import kz.shyngys.springbootfilestorage.dto.FileResponseDto;
import kz.shyngys.springbootfilestorage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<FileResponseDto>> upload(
            @RequestPart("file") FilePart filePart,
            @RequestParam("userId") Long userId
    ) {
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
                })
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

    @GetMapping
    public Mono<ResponseEntity<List<FileResponseDto>>> getAll() {
        return fileService.getAll()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<FileResponseDto>> getById(@PathVariable Long id) {
        return fileService.getById(id)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(
            @PathVariable Long id,
            @RequestParam("userId") Long userId
    ) {
        return fileService.delete(id, userId)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
