package classfit.example.classfit.event.service;

import static classfit.example.classfit.common.exception.ClassfitException.EVENT_NOT_FOUND;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.event.domain.Event;
import classfit.example.classfit.event.dto.request.EventCreateRequest;
import classfit.example.classfit.event.dto.request.EventDragUpdate;
import classfit.example.classfit.event.dto.request.EventModalRequest;
import classfit.example.classfit.event.dto.response.EventModalResponse;
import classfit.example.classfit.event.dto.response.EventMonthlyResponse;
import classfit.example.classfit.event.dto.response.EventResponse;
import classfit.example.classfit.event.repository.EventRepository;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.memberCalendar.domain.CalendarType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventCreateService eventCreationService;
    private final EventUpdateService eventUpdateService;
    private final EventGetService eventGetService;

    public EventResponse createEvent(Member member, EventCreateRequest request) {
        return eventCreationService.createEvent(member, request);
    }

    public EventResponse createModalEvent(Member member, EventModalRequest request) {
        return eventCreationService.createModalEvent(member, request);
    }

    public EventResponse updateEvent(Member member, long eventId, EventModalRequest request) {
        return eventUpdateService.updateEvent(member, eventId, request);
    }

    public EventModalResponse getEvent(long eventId) {
        return eventGetService.getEvent(eventId);
    }

    public List<EventMonthlyResponse> getMonthlyEventsByCalendarType(
        CalendarType calendarType,
        int year,
        int month
    ) {
        return eventGetService.getMonthlyEventsByCalendarType(calendarType, year, month);
    }

    @Transactional
    public void deleteEvent(long eventId) {
        Event event = getEventById(eventId);
        eventRepository.delete(event);
    }

    private Event getEventById(long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new ClassfitException(EVENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public EventResponse dragUpdateEvent(Long eventId, EventDragUpdate request) {
        return eventUpdateService.dragUpdateEvent(eventId, request);
    }
}