package kz.shyngys.springbootfilestorage.service.impl;

import kz.shyngys.springbootfilestorage.dto.FileResponseDto;
import kz.shyngys.springbootfilestorage.model.Event;
import kz.shyngys.springbootfilestorage.model.File;
import kz.shyngys.springbootfilestorage.model.User;
import kz.shyngys.springbootfilestorage.model.enumerated.EventStatus;
import kz.shyngys.springbootfilestorage.model.enumerated.FileStatus;
import kz.shyngys.springbootfilestorage.repository.EventRepository;
import kz.shyngys.springbootfilestorage.repository.FileRepository;
import kz.shyngys.springbootfilestorage.repository.UserRepository;
import kz.shyngys.springbootfilestorage.service.FileService;
import kz.shyngys.springbootfilestorage.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final S3Service s3Service;
    private final FileRepository fileRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public Mono<FileResponseDto> upload(String filename, String contentType, byte[] bytes, Long userId) {
        return Mono.fromCallable(() -> {
            String location = s3Service.upload(filename, contentType, bytes);

            File file = File.builder()
                    .name(filename)
                    .location(location)
                    .status(FileStatus.ACTIVE)
                    .build();
            File saved = fileRepository.save(file);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            Event event = Event.builder()
                    .user(user)
                    .file(saved)
                    .status(EventStatus.CREATED)
                    .timestamp(LocalDateTime.now())
                    .build();
            eventRepository.save(event);

            return toDto(saved);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<FileResponseDto> getById(Long id) {
        return Mono.fromCallable(() ->
                fileRepository.findById(id)
                        .filter(f -> f.getStatus() != FileStatus.ARCHIVED)
                        .map(this::toDto)
                        .orElseThrow(() -> new RuntimeException("File not found: " + id))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<List<FileResponseDto>> getAll() {
        return Mono.fromCallable(() ->
                fileRepository.findAllByStatus(FileStatus.ACTIVE)
                        .stream()
                        .map(this::toDto)
                        .toList()
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> delete(Long id, Long userId) {
        return Mono.fromRunnable(() -> {
            File file = fileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("File not found: " + id));

            s3Service.delete(file.getLocation());

            file.setStatus(FileStatus.ARCHIVED);
            fileRepository.save(file);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            Event event = Event.builder()
                    .user(user)
                    .file(file)
                    .status(EventStatus.DELETED)
                    .timestamp(LocalDateTime.now())
                    .build();
            eventRepository.save(event);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    private FileResponseDto toDto(File file) {
        return FileResponseDto.builder()
                .id(file.getId())
                .name(file.getName())
                .location(file.getLocation())
                .status(file.getStatus())
                .build();
    }
}