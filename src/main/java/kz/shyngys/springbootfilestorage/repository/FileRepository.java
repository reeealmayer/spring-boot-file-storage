package kz.shyngys.springbootfilestorage.repository;

import kz.shyngys.springbootfilestorage.model.FileEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends R2dbcRepository<FileEntity, Long> {
}
