package kz.shyngys.springbootfilestorage.repository;

import kz.shyngys.springbootfilestorage.model.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends R2dbcRepository<UserEntity, Long> {
}
