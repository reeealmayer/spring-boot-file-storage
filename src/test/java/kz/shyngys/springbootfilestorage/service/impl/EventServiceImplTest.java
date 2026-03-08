package kz.shyngys.springbootfilestorage.service.impl;

import kz.shyngys.springbootfilestorage.model.Event;
import kz.shyngys.springbootfilestorage.model.File;
import kz.shyngys.springbootfilestorage.model.User;
import kz.shyngys.springbootfilestorage.model.enumerated.EventStatus;
import kz.shyngys.springbootfilestorage.model.enumerated.FileStatus;
import kz.shyngys.springbootfilestorage.model.enumerated.UserRole;
import kz.shyngys.springbootfilestorage.model.enumerated.UserStatus;
import kz.shyngys.springbootfilestorage.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private User user;
    private File file;
    private Event event;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("alice")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();

        file = File.builder()
                .id(10L)
                .name("report.pdf")
                .location("http://localhost:9000/files/uuid_report.pdf")
                .status(FileStatus.ACTIVE)
                .build();

        event = Event.builder()
                .id(100L)
                .user(user)
                .file(file)
                .status(EventStatus.CREATED)
                .timestamp(LocalDateTime.of(2024, 6, 1, 12, 0))
                .build();
    }


    @Test
    void getById_existingId_shouldReturnDto() {
        when(eventRepository.findById(100L)).thenReturn(Optional.of(event));

        StepVerifier.create(eventService.getById(100L))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(100L);
                    assertThat(dto.getUserId()).isEqualTo(1L);
                    assertThat(dto.getUsername()).isEqualTo("alice");
                    assertThat(dto.getFileId()).isEqualTo(10L);
                    assertThat(dto.getFilename()).isEqualTo("report.pdf");
                    assertThat(dto.getStatus()).isEqualTo(EventStatus.CREATED);
                    assertThat(dto.getTimestamp()).isEqualTo(LocalDateTime.of(2024, 6, 1, 12, 0));
                })
                .verifyComplete();
    }

    @Test
    void getById_notFound_shouldThrowRuntimeException() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        StepVerifier.create(eventService.getById(999L))
                .expectErrorMatches(ex ->
                        ex instanceof RuntimeException && ex.getMessage().contains("not found"))
                .verify();
    }

    @Test
    void getById_eventWithNullUserAndFile_shouldReturnDtoWithNulls() {
        Event orphan = Event.builder()
                .id(200L)
                .user(null)
                .file(null)
                .status(EventStatus.DELETED)
                .timestamp(LocalDateTime.now())
                .build();
        when(eventRepository.findById(200L)).thenReturn(Optional.of(orphan));

        StepVerifier.create(eventService.getById(200L))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(200L);
                    assertThat(dto.getUserId()).isNull();
                    assertThat(dto.getUsername()).isNull();
                    assertThat(dto.getFileId()).isNull();
                    assertThat(dto.getFilename()).isNull();
                })
                .verifyComplete();
    }


    @Test
    void getAll_shouldReturnAllEvents() {
        Event second = Event.builder()
                .id(101L).user(user).file(file)
                .status(EventStatus.UPDATED)
                .timestamp(LocalDateTime.now())
                .build();
        when(eventRepository.findAll()).thenReturn(List.of(event, second));

        StepVerifier.create(eventService.getAll())
                .assertNext(list -> {
                    assertThat(list).hasSize(2);
                    assertThat(list.get(0).getId()).isEqualTo(100L);
                    assertThat(list.get(1).getId()).isEqualTo(101L);
                })
                .verifyComplete();
    }

    @Test
    void getAll_emptyRepository_shouldReturnEmptyList() {
        when(eventRepository.findAll()).thenReturn(List.of());

        StepVerifier.create(eventService.getAll())
                .assertNext(list -> assertThat(list).isEmpty())
                .verifyComplete();
    }


    @Test
    void getByUserId_shouldReturnOnlyUserEvents() {
        when(eventRepository.findAllByUserId(1L)).thenReturn(List.of(event));

        StepVerifier.create(eventService.getByUserId(1L))
                .assertNext(list -> {
                    assertThat(list).hasSize(1);
                    assertThat(list.get(0).getUserId()).isEqualTo(1L);
                })
                .verifyComplete();
    }

    @Test
    void getByUserId_noEvents_shouldReturnEmptyList() {
        when(eventRepository.findAllByUserId(42L)).thenReturn(List.of());

        StepVerifier.create(eventService.getByUserId(42L))
                .assertNext(list -> assertThat(list).isEmpty())
                .verifyComplete();
    }


    @Test
    void getByFileId_shouldReturnEventsForFile() {
        when(eventRepository.findAllByFileId(10L)).thenReturn(List.of(event));

        StepVerifier.create(eventService.getByFileId(10L))
                .assertNext(list -> {
                    assertThat(list).hasSize(1);
                    assertThat(list.get(0).getFileId()).isEqualTo(10L);
                    assertThat(list.get(0).getFilename()).isEqualTo("report.pdf");
                })
                .verifyComplete();
    }


    @Test
    void delete_existingId_shouldCallRepository() {
        when(eventRepository.existsById(100L)).thenReturn(true);
        doNothing().when(eventRepository).deleteById(100L);

        StepVerifier.create(eventService.delete(100L))
                .verifyComplete();

        verify(eventRepository, times(1)).deleteById(100L);
    }

    @Test
    void delete_notFound_shouldThrowRuntimeException() {
        when(eventRepository.existsById(999L)).thenReturn(false);

        StepVerifier.create(eventService.delete(999L))
                .expectErrorMatches(ex ->
                        ex instanceof RuntimeException && ex.getMessage().contains("not found"))
                .verify();

        verify(eventRepository, never()).deleteById(any());
    }
}