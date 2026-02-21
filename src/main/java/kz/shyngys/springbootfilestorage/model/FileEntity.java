package kz.shyngys.springbootfilestorage.model;

import kz.shyngys.springbootfilestorage.model.enumerated.FileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@Table(name = "files")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileEntity {
    private Long id;
    private String name;
    private String location;
    private FileStatus status;
    private List<EventEntity> events = new ArrayList<>();
}
