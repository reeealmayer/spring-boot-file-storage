package kz.shyngys.springbootfilestorage.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.shyngys.springbootfilestorage.model.enumerated.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "events")
@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private FileEntity file;
    @Enumerated(value = EnumType.STRING)
    private EventStatus status;
    private LocalDateTime timestamp;
}
