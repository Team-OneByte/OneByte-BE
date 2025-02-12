package classfit.example.classfit.calendar.event.service;

import classfit.example.classfit.calendar.category.domain.Category;
import classfit.example.classfit.calendar.category.repository.CategoryRepository;
import classfit.example.classfit.calendar.event.domain.Event;
import classfit.example.classfit.calendar.event.domain.enumType.EventRepeatType;
import classfit.example.classfit.calendar.event.dto.request.EventCreateRequest;
import classfit.example.classfit.calendar.event.dto.request.EventModalRequest;
import classfit.example.classfit.calendar.event.repository.EventRepository;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.calendar.memberCalendar.domain.MemberCalendar;
import classfit.example.classfit.calendar.memberCalendar.repository.MemberCalendarRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventRepeatService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final MemberCalendarRepository memberCalendarRepository;

    public void addRepeatedEvents(Member member, EventCreateRequest request) {
        if (request.eventRepeatType() == EventRepeatType.NONE) {
            return;
        }

        LocalDateTime currentStartDate = getNextRepeatDate(request.startDate(), request.eventRepeatType());
        LocalDateTime currentEndDate = getNextRepeatDate(request.endDate(), request.eventRepeatType());
        LocalDateTime repeatEndDate = request.repeatEndDate().orElse(currentStartDate.plusMonths(6));

        generateRepeatedEvents(member, request, currentStartDate, currentEndDate, repeatEndDate);
    }

    private void generateRepeatedEvents(
        Member member,
        EventCreateRequest request,
        LocalDateTime currentStartDate,
        LocalDateTime currentEndDate,
        LocalDateTime repeatEndDate
    ) {
        EventRepeatType eventRepeatType = request.eventRepeatType();

        while (shouldCreateEvent(currentEndDate, repeatEndDate)) {
            Event repeatedEvent = buildEventWithUpdatedDates(member, request, currentStartDate, currentEndDate);
            eventRepository.save(repeatedEvent);

            currentStartDate = getNextRepeatDate(currentStartDate, eventRepeatType);
            currentEndDate = getNextRepeatDate(currentEndDate, eventRepeatType);
        }
    }

    private Event buildEventWithUpdatedDates(
        Member member,
        EventCreateRequest request,
        LocalDateTime currentStartDate,
        LocalDateTime currentEndDate
    ) {
        Category category = categoryRepository.findById(request.categoryId());
        MemberCalendar memberCalendar = memberCalendarRepository.findByMemberAndType(member, request.calendarType());

        Event event = Event.builder()
            .name(request.name())
            .eventType(request.eventType())
            .category(category)
            .memberCalendar(memberCalendar)
            .startDate(currentStartDate)
            .endDate(currentEndDate)
            .isAllDay(request.isAllDay())
            .eventRepeatType(request.eventRepeatType())
            .repeatEndDate(request.repeatEndDate().orElse(null))
            .location(request.location().orElse(null))
            .memo(request.memo().orElse(null))
            .build();
        event.setDates(request.isAllDay(), currentStartDate, currentEndDate);
        return event;
    }

    public void addRepeatedModalEvents(Member member, EventModalRequest request) {
        if (request.eventRepeatType() == EventRepeatType.NONE) {
            return;
        }

        LocalDateTime currentStartDate = getNextRepeatDate(request.startDate(), request.eventRepeatType());
        LocalDateTime currentEndDate = getNextRepeatDate(request.endDate(), request.eventRepeatType());
        LocalDateTime repeatEndDate = request.repeatEndDate().orElse(currentStartDate.plusMonths(6));

        generateRepeatedModalEvents(member, request, currentStartDate, currentEndDate, repeatEndDate);
    }

    private void generateRepeatedModalEvents(
        Member member,
        EventModalRequest request,
        LocalDateTime currentStartDate,
        LocalDateTime currentEndDate,
        LocalDateTime repeatEndDate
    ) {
        EventRepeatType eventRepeatType = request.eventRepeatType();

        while (shouldCreateEvent(currentEndDate, repeatEndDate)) {
            Event repeatedEvent = buildModalEventWithUpdatedDates(member, request, currentStartDate, currentEndDate);
            eventRepository.save(repeatedEvent);

            currentStartDate = getNextRepeatDate(currentStartDate, eventRepeatType);
            currentEndDate = getNextRepeatDate(currentEndDate, eventRepeatType);
        }
    }

    private Event buildModalEventWithUpdatedDates(
        Member member,
        EventModalRequest request,
        LocalDateTime currentStartDate,
        LocalDateTime currentEndDate
    ) {
        Category category = categoryRepository.findById(request.categoryId());
        MemberCalendar memberCalendar = memberCalendarRepository.findByMemberAndType(member, request.calendarType());

        Event event = Event.builder()
            .name(request.name())
            .category(category)
            .memberCalendar(memberCalendar)
            .eventType(request.eventType())
            .startDate(currentStartDate)
            .endDate(currentEndDate)
            .isAllDay(request.isAllDay())
            .eventRepeatType(request.eventRepeatType())
            .repeatEndDate(request.repeatEndDate().orElse(null))
            .build();
        event.setDates(request.isAllDay(), currentStartDate, currentEndDate);
        return event;
    }

    private boolean shouldCreateEvent(LocalDateTime currentEndDate, LocalDateTime repeatEndDate) {
        return currentEndDate.isBefore(repeatEndDate) || currentEndDate.isEqual(repeatEndDate);
    }

    private LocalDateTime getNextRepeatDate(LocalDateTime date, EventRepeatType eventRepeatType) {
        return switch (eventRepeatType) {
            case DAILY -> date.plusDays(1);
            case WEEKLY -> date.plusWeeks(1);
            case MONTHLY -> date.plusMonths(1);
            case YEARLY -> date.plusYears(1);
            default -> throw new IllegalArgumentException("유효한 반복 타입을 지정해주세요.");
        };
    }
}
