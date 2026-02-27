package kz.shyngys.springbootfilestorage.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import kz.shyngys.springbootfilestorage.model.enumerated.FileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "files")
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    @Enumerated(EnumType.STRING)
    private FileStatus status;
    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventEntity> events = new ArrayList<>();
}
