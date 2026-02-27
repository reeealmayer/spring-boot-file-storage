package kz.shyngys.springbootfilestorage.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import kz.shyngys.springbootfilestorage.model.enumerated.UserRole;
import kz.shyngys.springbootfilestorage.model.enumerated.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "users")
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "events")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    @Enumerated(value = EnumType.STRING)
    private UserRole role;
    @Enumerated(value = EnumType.STRING)
    private UserStatus status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventEntity> events = new ArrayList<>();
}
