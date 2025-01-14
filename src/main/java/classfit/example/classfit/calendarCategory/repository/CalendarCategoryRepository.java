package classfit.example.classfit.calendarCategory.repository;

import classfit.example.classfit.calendarCategory.domain.CalendarCategory;
import classfit.example.classfit.memberCalendar.domain.CalendarType;
import classfit.example.classfit.memberCalendar.domain.MemberCalendar;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarCategoryRepository extends JpaRepository<CalendarCategory, Long> {
    boolean existsByNameAndIdNot(String name, Long id);
    List<CalendarCategory> findByMemberCalendar(MemberCalendar memberCalendar);
    CalendarCategory findById(Optional<Long> aLong);
}
