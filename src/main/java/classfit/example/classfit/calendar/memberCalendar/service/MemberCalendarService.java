package classfit.example.classfit.calendar.memberCalendar.service;

import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.calendar.memberCalendar.domain.enumType.CalendarType;
import classfit.example.classfit.calendar.memberCalendar.domain.MemberCalendar;
import classfit.example.classfit.calendar.memberCalendar.repository.MemberCalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberCalendarService {
    private final MemberCalendarRepository memberCalendarRepository;

    public void createPersonalCalendar(Member member) {
        MemberCalendar personalCalendar = new MemberCalendar(member, CalendarType.PERSONAL);
        memberCalendarRepository.save(personalCalendar);
    }

    public void createSharedCalendar(Member member) {
        MemberCalendar sharedCalendar = new MemberCalendar(member, CalendarType.SHARED);
        memberCalendarRepository.save(sharedCalendar);
    }

    public MemberCalendar getMemberCalendar(Member member, CalendarType type) {
        return memberCalendarRepository.findByMemberAndType(member, type);
    }
}
