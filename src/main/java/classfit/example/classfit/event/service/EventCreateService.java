package classfit.example.classfit.event.service;

import classfit.example.classfit.calendarCategory.domain.CalendarCategory;
import classfit.example.classfit.calendarCategory.repository.CalendarCategoryRepository;
import classfit.example.classfit.event.domain.Event;
import classfit.example.classfit.event.dto.request.EventCreateRequest;
import classfit.example.classfit.event.dto.request.EventModalRequest;
import classfit.example.classfit.event.dto.response.EventResponse;
import classfit.example.classfit.event.repository.EventRepository;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.service.MemberService;
import classfit.example.classfit.memberCalendar.domain.MemberCalendar;
import classfit.example.classfit.memberCalendar.repository.MemberCalendarRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventCreateService {
    private final EventRepository eventRepository;
    private final EventRepeatService eventRepeatService;
    private final CalendarCategoryRepository calendarCategoryRepository;
    private final MemberCalendarRepository memberCalendarRepository;
    private final MemberService memberService;

    @Transactional
    public EventResponse createEvent(Member member, EventCreateRequest request) {
        Event event = buildEvent(member, request);
        Event savedEvent = eventRepository.save(event);

        addAttendeesToEvent(savedEvent, request.memberIds());
        eventRepeatService.addRepeatedEvents(member, request);

        return EventResponse.of(
            savedEvent.getId(),
            savedEvent.getName(),
            savedEvent.getEventType(),
            savedEvent.getStartDate(),
            savedEvent.getEndDate()
        );
    }

    private Event buildEvent(Member member, EventCreateRequest request) {
        CalendarCategory category = calendarCategoryRepository.findById(request.categoryId());
        MemberCalendar memberCalendar = memberCalendarRepository.findByMemberAndType(member, request.calendarType());

        Event event = Event.builder()
            .name(request.name())
            .eventType(request.eventType())
            .category(category)
            .memberCalendar(memberCalendar)
            .startDate(request.startDate())
            .endDate(request.getEndDate())
            .isAllDay(request.isAllDay())
            .eventRepeatType(request.eventRepeatType())
            .repeatEndDate(request.repeatEndDate().orElse(null))
            .location(request.location().orElse(null))
            .memo(request.memo().orElse(null))
            .build();
        event.setDates(request.isAllDay(), request.startDate(), request.getEndDate());
        return event;
    }

    private void addAttendeesToEvent(Event event, List<Long> memberIds) {
        for (Long memberId : memberIds) {
            Member member = memberService.getMembers(memberId);
            event.addAttendee(member);
        }
    }

    @Transactional
    public EventResponse createModalEvent(Member member, EventModalRequest request) {
        Event event = buildModalEvent(member, request);
        Event savedEvent = eventRepository.save(event);

        eventRepeatService.addRepeatedModalEvents(member, request);

        return EventResponse.of(
            savedEvent.getId(),
            savedEvent.getName(),
            savedEvent.getEventType(),
            savedEvent.getStartDate(),
            savedEvent.getEndDate()
        );
    }

    private Event buildModalEvent(Member member, EventModalRequest request) {
        CalendarCategory category = calendarCategoryRepository.findById(request.categoryId());
        MemberCalendar memberCalendar = memberCalendarRepository.findByMemberAndType(member, request.calendarType());

        Event event = Event.builder()
            .name(request.name())
            .category(category)
            .memberCalendar(memberCalendar)
            .eventType(request.eventType())
            .startDate(request.startDate())
            .endDate(request.getEndDate())
            .isAllDay(request.isAllDay())
            .eventRepeatType(request.eventRepeatType())
            .repeatEndDate(request.repeatEndDate().orElse(null))
            .build();
        event.setDates(request.isAllDay(), request.startDate(), request.getEndDate());
        return event;
    }
}
