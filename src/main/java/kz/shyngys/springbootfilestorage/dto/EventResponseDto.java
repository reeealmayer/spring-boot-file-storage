package kz.shyngys.springbootfilestorage.dto;

import kz.shyngys.springbootfilestorage.model.enumerated.EventStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventResponseDto {
    private Long id;
    private Long userId;
    private Long fileId;
    private EventStatus status;
    private LocalDateTime timestamp;
}