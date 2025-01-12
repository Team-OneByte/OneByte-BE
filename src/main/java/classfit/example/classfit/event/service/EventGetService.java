package classfit.example.classfit.event.service;

import static classfit.example.classfit.common.exception.ClassfitException.EVENT_NOT_FOUND;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.event.domain.Event;
import classfit.example.classfit.event.dto.response.EventMontylyResponse;
import classfit.example.classfit.event.dto.response.EventResponse;
import classfit.example.classfit.event.repository.EventRepository;
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
public class EventGetService {
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public EventResponse getEvent(long eventId) {
        Event event = getEventById(eventId);
        return Event.buildEventResponse(event);
    }

    private Event getEventById(long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new ClassfitException(EVENT_NOT_FOUND, HttpStatus.NOT_FOUND));
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
}
