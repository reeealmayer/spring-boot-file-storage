package kz.shyngys.springbootfilestorage.repository;

import kz.shyngys.springbootfilestorage.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
}
