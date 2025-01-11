package classfit.example.classfit.event.service;

import static classfit.example.classfit.common.exception.ClassfitException.EVENT_NOT_FOUND;

import classfit.example.classfit.calendarCategory.domain.CalendarCategory;
import classfit.example.classfit.calendarCategory.service.CalendarCategoryService;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.event.domain.Event;
import classfit.example.classfit.event.domain.EventRepeatType;
import classfit.example.classfit.event.dto.request.EventCreateRequest;
import classfit.example.classfit.event.dto.request.EventModalRequest;
import classfit.example.classfit.event.dto.response.EventMontylyResponse;
import classfit.example.classfit.event.dto.response.EventResponse;
import classfit.example.classfit.event.repository.EventRepository;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.service.MemberService;
import classfit.example.classfit.memberCalendar.domain.CalendarType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final CalendarCategoryService calendarCategoryService;
    private final MemberService memberService;

    @Transactional
    public EventResponse createEvent(EventCreateRequest request) {
        Event event = buildEvent(request);
        Event savedEvent = eventRepository.save(event);

        addRepeatedEvents(request);

        addAttendeesToEvent(savedEvent, request.memberIds());
        return EventResponse.of(savedEvent.getId(), savedEvent.getName(), savedEvent.getEventType(), savedEvent.getStartDate(), savedEvent.getEndDate());
    }

    private Event buildEvent(EventCreateRequest request) {
        CalendarCategory category = calendarCategoryService.getCategoryById(request.categoryId());

        Event event = Event.builder()
            .name(request.name())
            .eventType(request.eventType())
            .category(category)
            .startDate(request.startDate())
            .endDate(request.getEndDate())
            .isAllDay(request.isAllDay())
            .eventRepeatType(request.eventRepeatType().orElse(EventRepeatType.NONE))
            .repeatEndDate(request.repeatEndDate().orElse(null))
            .location(request.location().orElse(null))
            .memo(request.memo().orElse(null))
            .build();
        event.setDates(request.isAllDay(), request.startDate(), request.getEndDate());
        return event;
    }

    private void addRepeatedEvents(EventCreateRequest request) {
        if (request.eventRepeatType().orElse(EventRepeatType.NONE) == EventRepeatType.NONE) {
            return;
        }

        LocalDateTime currentStartDate = request.startDate();
        LocalDateTime currentEndDate = request.endDate();
        EventRepeatType eventRepeatType = request.eventRepeatType().orElse(EventRepeatType.NONE);
        LocalDateTime repeatEndDate = request.repeatEndDate().orElse(null);

        while (shouldCreateEvent(currentEndDate, repeatEndDate)) {
            Event repeatedEvent = buildEventWithUpdatedDates(request, currentStartDate, currentEndDate);
            eventRepository.save(repeatedEvent);

            currentStartDate = getNextRepeatDate(currentStartDate, eventRepeatType);
            currentEndDate = getNextRepeatDate(currentEndDate, eventRepeatType);
        }
    }

    private boolean shouldCreateEvent(LocalDateTime currentEndDate, LocalDateTime repeatEndDate) {
        return currentEndDate.isBefore(repeatEndDate) || currentEndDate.isEqual(repeatEndDate);
    }

    private Event buildEventWithUpdatedDates(EventCreateRequest request, LocalDateTime currentStartDate, LocalDateTime currentEndDate) {
        Event event = buildEvent(request);
        return event.toBuilder()
            .startDate(currentStartDate)
            .endDate(currentEndDate)
            .build();
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

    private void addAttendeesToEvent(Event event, List<Long> memberIds) {
        for (Long memberId : memberIds) {
            Member member = memberService.getMembers(memberId);
            event.addAttendee(member);
        }
    }

    @Transactional(readOnly = true)
    public List<EventMontylyResponse> getMonthlyEventsByCalendarType(CalendarType calendarType, int year, int month) {
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);

        List<Event> events = eventRepository.findByCalendarTypeAndStartDateBetween(calendarType, startOfMonth, endOfMonth);

        return mapToEventCreateResponse(events);
    }

    private List<EventMontylyResponse> mapToEventCreateResponse(List<Event> events) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return events.stream()
            .map(event -> EventMontylyResponse.of(
                String.valueOf(event.getId()),
                event.getName(),
                event.getEventType().toString(),
                event.getStartDate().format(formatter),
                event.getEndDate().format(formatter)))
            .collect(Collectors.toList());
    }

    @Transactional
    public EventResponse createModalEvent(EventModalRequest request) {
        Event event = buildModalEvent(request);
        Event savedEvent = eventRepository.save(event);

        addRepeatedModalEvents(request);

        return EventResponse.of(savedEvent.getId(), savedEvent.getName(), savedEvent.getEventType(), savedEvent.getStartDate(), savedEvent.getEndDate());
    }

    private Event buildModalEvent(EventModalRequest request) {
        CalendarCategory category = calendarCategoryService.getCategoryById(request.categoryId());

        Event event = Event.builder()
            .name(request.name())
            .category(category)
            .eventType(request.eventType())
            .startDate(request.startDate())
            .endDate(request.getEndDate())
            .isAllDay(request.isAllDay())
            .eventRepeatType(request.eventRepeatType().orElse(EventRepeatType.NONE))
            .repeatEndDate(request.repeatEndDate().orElse(null))
            .build();
        event.setDates(request.isAllDay(), request.startDate(), request.getEndDate());
        return event;
    }

    private void addRepeatedModalEvents(EventModalRequest request) {
        if (request.eventRepeatType().orElse(EventRepeatType.NONE) == EventRepeatType.NONE) {
            return;
        }

        LocalDateTime currentStartDate = request.startDate();
        LocalDateTime currentEndDate = request.endDate();
        EventRepeatType eventRepeatType = request.eventRepeatType().orElse(EventRepeatType.NONE);
        LocalDateTime repeatEndDate = request.repeatEndDate().orElse(null);

        while (shouldCreateEvent(currentEndDate, repeatEndDate)) {
            Event repeatedEvent = buildModalEventWithUpdatedDates(request, currentStartDate, currentEndDate);
            eventRepository.save(repeatedEvent);

            currentStartDate = getNextRepeatDate(currentStartDate, eventRepeatType);
            currentEndDate = getNextRepeatDate(currentEndDate, eventRepeatType);
        }
    }

    private Event buildModalEventWithUpdatedDates(EventModalRequest request, LocalDateTime currentStartDate, LocalDateTime currentEndDate) {
        Event event = buildModalEvent(request);
        return event.toBuilder()
            .startDate(currentStartDate)
            .endDate(currentEndDate)
            .build();
    }

    @Transactional(readOnly = true)
    public EventResponse getEvent(long eventId) {
        Event event = getEventById(eventId);
        return EventResponse.of(event.getId(), event.getName(), event.getEventType(), event.getStartDate(), event.getEndDate());
    }

    private Event getEventById(long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new ClassfitException(EVENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public EventResponse updateEvent(long eventId, EventModalRequest request) {
        Event event = getEventById(eventId);
        CalendarCategory category = calendarCategoryService.getCategoryById(request.categoryId());

        event.update(
            request.name(),
            category,
            request.eventType(),
            request.startDate(),
            request.getEndDate(),
            request.isAllDay()
        );

        eventRepository.save(event);
        return EventResponse.of(event.getId(), event.getName(), event.getEventType(), event.getStartDate(), event.getEndDate());
    }

    @Transactional
    public void deleteEvent(long eventId) {
        Event event = getEventById(eventId);
        eventRepository.delete(event);
    }
}
