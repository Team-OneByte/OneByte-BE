package classfit.example.classfit.event.repository;

import classfit.example.classfit.calendarCategory.domain.CalendarCategory;
import classfit.example.classfit.event.domain.Event;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<List<Event>> findByCategoryAndStartDateBetween(CalendarCategory category, LocalDateTime startOfMonth, LocalDateTime endOfMonth);

    Optional<Event> findById(long eventId);
}
