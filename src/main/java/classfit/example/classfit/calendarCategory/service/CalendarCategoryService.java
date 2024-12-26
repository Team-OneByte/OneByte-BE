package classfit.example.classfit.calendarCategory.service;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.calendarCategory.domain.CalendarCategory;
import classfit.example.classfit.calendarCategory.dto.request.CalendarCategoryCreateRequest;
import classfit.example.classfit.calendarCategory.dto.response.CalendarCategoryCreateResponse;
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
    public CalendarCategoryCreateResponse addCategory(@AuthMember Member member, CalendarCategoryCreateRequest request) {
        CalendarCategory category = buildCategory(member, request);
        CalendarCategory savedCategory = calendarCategoryRepository.save(category);
        return CalendarCategoryCreateResponse.of(savedCategory.getId(), savedCategory.getName(), savedCategory.getColor(), savedCategory.getMemberCalendar().getType());
    }

    private CalendarCategory buildCategory(Member member, CalendarCategoryCreateRequest request) {
        MemberCalendar memberCalendar = memberCalendarService.getMemberCalendar(member, request.type());
        String uniqueName = createUniqueCategoryName(request.name());

        return CalendarCategory.builder()
            .name(uniqueName)
            .color(request.color())
            .memberCalendar(memberCalendar)
            .build();
    }

    private String createUniqueCategoryName(String baseName) {
        String uniqueName = baseName;
        int count = 1;

        while (calendarCategoryRepository.existsByName(uniqueName)) {
            uniqueName = baseName + " (" + count + ")";
            count++;
        }
        return uniqueName;
    }
}
