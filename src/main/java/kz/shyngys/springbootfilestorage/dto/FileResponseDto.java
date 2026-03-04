package kz.shyngys.springbootfilestorage.dto;

import kz.shyngys.springbootfilestorage.model.enumerated.FileStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileResponseDto {
    private Long id;
    private String name;
    private String location;
    private FileStatus status;
}

