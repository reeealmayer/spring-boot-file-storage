package kz.shyngys.springbootfilestorage.service;

import kz.shyngys.springbootfilestorage.dto.EventResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EventService {
    Mono<EventResponseDto> getById(Long id);

    Mono<List<EventResponseDto>> getAll();

    Mono<List<EventResponseDto>> getByUserId(Long userId);

    Mono<List<EventResponseDto>> getByFileId(Long fileId);

    Mono<Void> delete(Long id);
}