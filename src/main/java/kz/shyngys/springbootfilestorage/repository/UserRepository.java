package kz.shyngys.springbootfilestorage.repository;

import kz.shyngys.springbootfilestorage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
