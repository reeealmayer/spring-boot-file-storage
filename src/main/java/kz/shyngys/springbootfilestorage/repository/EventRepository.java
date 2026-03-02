package kz.shyngys.springbootfilestorage.repository;

import kz.shyngys.springbootfilestorage.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
