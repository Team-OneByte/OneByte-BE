package classfit.example.classfit.event.service;

import static classfit.example.classfit.common.exception.ClassfitException.CATEGORY_NOT_FOUND;
import static classfit.example.classfit.common.exception.ClassfitException.EVENT_NOT_FOUND;

import classfit.example.classfit.calendarCategory.domain.CalendarCategory;
import classfit.example.classfit.calendarCategory.service.CalendarCategoryService;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.event.domain.Event;
import classfit.example.classfit.event.domain.EventType;
import classfit.example.classfit.event.dto.request.EventCreateRequest;
import classfit.example.classfit.event.dto.response.EventCreateResponse;
import classfit.example.classfit.event.repository.EventRepository;
import java.time.LocalDateTime;
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

    @Transactional
    public EventCreateResponse createEvent(EventCreateRequest request) {
        Event event = buildEvent(request);
        Event savedEvent = eventRepository.save(event);
        return EventCreateResponse.of(savedEvent.getId(), savedEvent.getName(), savedEvent.getStartDate(), savedEvent.getEndDate());
    }

    private Event buildEvent(EventCreateRequest request) {
        CalendarCategory category = calendarCategoryService.getCategoryById(request.categoryId());

        return Event.builder()
            .name(request.name())
            .category(category)
            .eventType(EventType.SCHEDULE)
            .startDate(request.startDate())
            .endDate(request.endDate())
            .isAllDay(request.isAllDay())
            .isRepeating(request.isRepeating())
            .notificationTime(request.notificationTime())
            .location(request.location())
            .memo(request.memo())
            .build();
    }

    @Transactional(readOnly = true)
    public List<EventCreateResponse> getMonthlyEventsByCategory(long categoryId, int year, int month) {
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);

        CalendarCategory category = calendarCategoryService.getCategoryById(categoryId);
        List<Event> events = getEventsByCategoryAndStartDate(category, startOfMonth, endOfMonth);

        return mapToEventCreateResponse(events);
    }

    private List<Event> getEventsByCategoryAndStartDate(CalendarCategory category, LocalDateTime startOfMonth, LocalDateTime endOfMonth) {
        return eventRepository.findByCategoryAndStartDateBetween(category, startOfMonth, endOfMonth)
            .orElseThrow(() -> new ClassfitException(CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private List<EventCreateResponse> mapToEventCreateResponse(List<Event> events) {
        return events.stream()
            .map(event -> EventCreateResponse.of(
                event.getId(),
                event.getName(),
                event.getStartDate(),
                event.getEndDate()))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventCreateResponse getMonthlyEvent(long eventId) {
        Event event = getEventById(eventId);
        return EventCreateResponse.of(event.getId(), event.getName(), event.getStartDate(), event.getEndDate());
    }

    private Event getEventById(long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new ClassfitException(EVENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}
