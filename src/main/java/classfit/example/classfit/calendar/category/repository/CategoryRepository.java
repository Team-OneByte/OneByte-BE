package classfit.example.classfit.calendar.category.repository;

import classfit.example.classfit.calendar.category.domain.Category;
import classfit.example.classfit.calendar.memberCalendar.domain.enumType.CalendarType;
import classfit.example.classfit.calendar.memberCalendar.domain.MemberCalendar;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameAndIdNot(String name, Long id);
    List<Category> findByMemberCalendar(MemberCalendar memberCalendar);
    Category findById(Optional<Long> aLong);

    @Query("SELECT c FROM Category c " +
        "WHERE c.memberCalendar.type = :type " +
        "AND c.memberCalendar.member.academy.id = :academyId")
    List<Category> findByMemberCalendarTypeAndAcademyId(CalendarType type, Long academyId);
}
