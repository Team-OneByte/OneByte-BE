package classfit.example.classfit.calendarCategory.repository;

import classfit.example.classfit.calendarCategory.domain.CalendarCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarCategoryRepository extends JpaRepository<CalendarCategory, Long> {
}
