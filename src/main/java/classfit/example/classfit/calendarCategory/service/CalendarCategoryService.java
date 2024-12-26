package classfit.example.classfit.calendarCategory.service;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.calendarCategory.domain.CalendarCategory;
import classfit.example.classfit.calendarCategory.dto.request.CalendarCategoryCreateRequest;
import classfit.example.classfit.calendarCategory.dto.response.CalendarCategoryResponse;
import classfit.example.classfit.calendarCategory.repository.CalendarCategoryRepository;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.memberCalendar.domain.MemberCalendar;
import classfit.example.classfit.memberCalendar.service.MemberCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalendarCategoryService {
    private final CalendarCategoryRepository calendarCategoryRepository;
    private final MemberCalendarService memberCalendarService;

    @Transactional
    public CalendarCategoryResponse addCategory(@AuthMember Member member, CalendarCategoryCreateRequest request) {
        MemberCalendar memberCalendar = memberCalendarService.getMemberCalendar(member, request.type());

        CalendarCategory category = CalendarCategory.builder()
            .name(request.name())
            .color(request.color())
            .memberCalendar(memberCalendar)
            .build();

        CalendarCategory savedCategory = calendarCategoryRepository.save(category);
        return new CalendarCategoryResponse(savedCategory.getId(), savedCategory.getName(), savedCategory.getColor(), savedCategory.getMemberCalendar().getType());
    }
}
