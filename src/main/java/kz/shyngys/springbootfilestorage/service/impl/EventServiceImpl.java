package kz.shyngys.springbootfilestorage.service.impl;

import kz.shyngys.springbootfilestorage.dto.EventResponseDto;
import kz.shyngys.springbootfilestorage.model.Event;
import kz.shyngys.springbootfilestorage.repository.EventRepository;
import kz.shyngys.springbootfilestorage.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public Mono<EventResponseDto> getById(Long id) {
        return Mono.fromCallable(() ->
                eventRepository.findById(id)
                        .map(this::toDto)
                        .orElseThrow(() -> new RuntimeException("Event not found: " + id))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<List<EventResponseDto>> getAll() {
        return Mono.fromCallable(() ->
                eventRepository.findAll().stream().map(this::toDto).toList()
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<List<EventResponseDto>> getByUserId(Long userId) {
        return Mono.fromCallable(() ->
                eventRepository.findAllByUserId(userId).stream().map(this::toDto).toList()
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<List<EventResponseDto>> getByFileId(Long fileId) {
        return Mono.fromCallable(() ->
                eventRepository.findAllByFileId(fileId).stream().map(this::toDto).toList()
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> delete(Long id) {
        return Mono.fromRunnable(() -> {
            if (!eventRepository.existsById(id)) {
                throw new RuntimeException("Event not found: " + id);
            }
            eventRepository.deleteById(id);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    private EventResponseDto toDto(Event event) {
        return EventResponseDto.builder()
                .id(event.getId())
                .userId(event.getUser() != null ? event.getUser().getId() : null)
                .username(event.getUser() != null ? event.getUser().getUsername() : null)
                .fileId(event.getFile() != null ? event.getFile().getId() : null)
                .filename(event.getFile() != null ? event.getFile().getName() : null)
                .status(event.getStatus())
                .timestamp(event.getTimestamp())
                .build();
    }
}