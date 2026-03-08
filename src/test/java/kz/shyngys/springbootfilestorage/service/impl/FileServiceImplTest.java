package kz.shyngys.springbootfilestorage.service.impl;

import kz.shyngys.springbootfilestorage.model.Event;
import kz.shyngys.springbootfilestorage.model.File;
import kz.shyngys.springbootfilestorage.model.User;
import kz.shyngys.springbootfilestorage.model.enumerated.EventStatus;
import kz.shyngys.springbootfilestorage.model.enumerated.FileStatus;
import kz.shyngys.springbootfilestorage.model.enumerated.UserRole;
import kz.shyngys.springbootfilestorage.model.enumerated.UserStatus;
import kz.shyngys.springbootfilestorage.repository.EventRepository;
import kz.shyngys.springbootfilestorage.repository.FileRepository;
import kz.shyngys.springbootfilestorage.repository.UserRepository;
import kz.shyngys.springbootfilestorage.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private S3Service s3Service;
    @Mock
    private FileRepository fileRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FileServiceImpl fileService;

    private User testUser;
    private File testFile;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();

        testFile = File.builder()
                .id(1L)
                .name("test.txt")
                .location("http://localhost:9000/files/uuid_test.txt")
                .status(FileStatus.ACTIVE)
                .build();
    }

    @Test
    void upload_shouldSaveFileAndCreateEvent() throws Exception {
        when(s3Service.upload(anyString(), anyString(), any(byte[].class)))
                .thenReturn("http://localhost:9000/files/uuid_test.txt");
        when(fileRepository.save(any(File.class))).thenReturn(testFile);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventRepository.save(any(Event.class))).thenReturn(
                Event.builder().id(1L).user(testUser).file(testFile).status(EventStatus.CREATED).build()
        );

        StepVerifier.create(fileService.upload("test.txt", "text/plain", new byte[]{1, 2, 3}, 1L))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(1L);
                    assertThat(dto.getName()).isEqualTo("test.txt");
                    assertThat(dto.getStatus()).isEqualTo(FileStatus.ACTIVE);
                })
                .verifyComplete();

        verify(s3Service).upload(eq("test.txt"), eq("text/plain"), any());
        verify(fileRepository).save(any(File.class));
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void getById_shouldReturnFileDto() {
        when(fileRepository.findById(1L)).thenReturn(Optional.of(testFile));

        StepVerifier.create(fileService.getById(1L))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(1L);
                    assertThat(dto.getName()).isEqualTo("test.txt");
                })
                .verifyComplete();
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(fileRepository.findById(99L)).thenReturn(Optional.empty());

        StepVerifier.create(fileService.getById(99L))
                .expectErrorMatches(ex -> ex instanceof RuntimeException
                        && ex.getMessage().contains("not found"))
                .verify();
    }

    @Test
    void getAll_shouldReturnActiveFiles() {
        when(fileRepository.findAllByStatus(FileStatus.ACTIVE)).thenReturn(List.of(testFile));

        StepVerifier.create(fileService.getAll())
                .assertNext(list -> {
                    assertThat(list).hasSize(1);
                    assertThat(list.get(0).getStatus()).isEqualTo(FileStatus.ACTIVE);
                })
                .verifyComplete();
    }

    @Test
    void delete_shouldArchiveFileAndCreateEvent() {
        when(fileRepository.findById(1L)).thenReturn(Optional.of(testFile));
        when(fileRepository.save(any(File.class))).thenReturn(testFile);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventRepository.save(any(Event.class))).thenReturn(
                Event.builder().id(2L).user(testUser).file(testFile).status(EventStatus.DELETED).build()
        );
        doNothing().when(s3Service).delete(anyString());

        StepVerifier.create(fileService.delete(1L, 1L))
                .verifyComplete();

        verify(s3Service).delete(testFile.getLocation());
        verify(fileRepository).save(argThat(f -> f.getStatus() == FileStatus.ARCHIVED));
    }
}