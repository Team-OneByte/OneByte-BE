package classfit.example.classfit.calendar.event.repository;

import classfit.example.classfit.calendar.event.domain.Event;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.calendar.memberCalendar.domain.enumType.CalendarType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e " +
        "WHERE e.memberCalendar.type = :calendarType " +
        "AND e.memberCalendar.member = :member " +
        "AND e.startDate BETWEEN :startOfMonth AND :endOfMonth")
    List<Event> findByPersonalCalendarTypeAndStartDateBetween(
        @Param("calendarType") CalendarType calendarType,
        @Param("startOfMonth") LocalDateTime startOfMonth,
        @Param("endOfMonth") LocalDateTime endOfMonth,
        @Param("member")Member member
    );

    @Query("SELECT e FROM Event e " +
        "WHERE e.memberCalendar.type = :calendarType " +
        "AND e.memberCalendar.member.academy.id = :academyId " +
        "AND e.startDate BETWEEN :startOfMonth AND :endOfMonth")
    List<Event> findBySharedCalendarAndStartDateBetween(
        @Param("calendarType") CalendarType calendarType,
        @Param("startOfMonth") LocalDateTime startOfMonth,
        @Param("endOfMonth") LocalDateTime endOfMonth,
        @Param("academyId") Long academyId
    );

    Optional<Event> findById(long eventId);
}
