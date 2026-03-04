package kz.shyngys.springbootfilestorage.repository;

import kz.shyngys.springbootfilestorage.model.File;
import kz.shyngys.springbootfilestorage.model.enumerated.FileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findAllByStatus(FileStatus status);
}
