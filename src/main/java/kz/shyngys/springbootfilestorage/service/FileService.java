package kz.shyngys.springbootfilestorage.service;

import kz.shyngys.springbootfilestorage.dto.FileResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FileService {

    Mono<FileResponseDto> upload(String filename, String contentType, byte[] bytes, Long userId);

    Mono<FileResponseDto> getById(Long id);

    Mono<List<FileResponseDto>> getAll();

    Mono<List<FileResponseDto>> getByUserId(Long userId);

    Mono<Void> delete(Long id, Long userId);
}