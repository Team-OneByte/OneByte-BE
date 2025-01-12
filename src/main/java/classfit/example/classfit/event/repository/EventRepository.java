package classfit.example.classfit.event.repository;

import classfit.example.classfit.event.domain.Event;
import classfit.example.classfit.memberCalendar.domain.CalendarType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e WHERE e.memberCalendar.type = :calendarType " +
        "AND e.startDate BETWEEN :startOfMonth AND :endOfMonth")
    List<Event> findByCalendarTypeAndStartDateBetween(
        @Param("calendarType") CalendarType calendarType,
        @Param("startOfMonth") LocalDateTime startOfMonth,
        @Param("endOfMonth") LocalDateTime endOfMonth
    );
    Optional<Event> findById(long eventId);
}
