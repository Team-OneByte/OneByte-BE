package classfit.example.classfit.calendar.memberCalendar.repository;

import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.calendar.memberCalendar.domain.enumType.CalendarType;
import classfit.example.classfit.calendar.memberCalendar.domain.MemberCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberCalendarRepository extends JpaRepository<MemberCalendar, Long> {
    MemberCalendar findByMemberAndType(Member member, CalendarType type);
}
