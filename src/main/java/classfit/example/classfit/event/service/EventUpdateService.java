package classfit.example.classfit.event.service;

import static classfit.example.classfit.common.exception.ClassfitException.EVENT_NOT_FOUND;

import classfit.example.classfit.calendarCategory.domain.CalendarCategory;
import classfit.example.classfit.calendarCategory.repository.CalendarCategoryRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.event.domain.Event;
import classfit.example.classfit.event.dto.request.EventDragUpdate;
import classfit.example.classfit.event.dto.request.EventModalRequest;
import classfit.example.classfit.event.dto.response.EventResponse;
import classfit.example.classfit.event.repository.EventRepository;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.memberCalendar.domain.MemberCalendar;
import classfit.example.classfit.memberCalendar.repository.MemberCalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventUpdateService {
    private final EventRepository eventRepository;
    private final EventRepeatService eventRepeatService;
    private final CalendarCategoryRepository calendarCategoryRepository;
    private final MemberCalendarRepository memberCalendarRepository;

    @Transactional
    public EventResponse updateEvent(Member member, long eventId, EventModalRequest request) {
        Event event = getEventById(eventId);
        CalendarCategory category = calendarCategoryRepository.findById(request.categoryId());
        MemberCalendar memberCalendar = memberCalendarRepository.findByMemberAndType(member, request.calendarType());

        event.update(
            request.name(),
            category,
            request.eventType(),
            request.startDate(),
            request.getEndDate(),
            request.isAllDay(),
            memberCalendar
        );

        eventRepository.save(event);
        eventRepeatService.addRepeatedModalEvents(member, request);
        return Event.buildEventResponse(event);
    }

    @Transactional
    public EventResponse dragUpdateEvent(Long eventId, EventDragUpdate request) {
        Event event = getEventById(eventId);

        event.dragUpdate(
            request.startDate(),
            request.endDate()
        );

        eventRepository.save(event);
        return Event.buildEventResponse(event);
    }

    private Event getEventById(long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new ClassfitException(EVENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}
