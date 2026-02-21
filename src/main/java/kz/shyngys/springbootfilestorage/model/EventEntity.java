package kz.shyngys.springbootfilestorage.model;

import kz.shyngys.springbootfilestorage.model.enumerated.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "events")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {
    @Id
    private Long id;
    private UserEntity user;
    private FileEntity file;
    private EventStatus status;
    private LocalDateTime timestamp;
}
