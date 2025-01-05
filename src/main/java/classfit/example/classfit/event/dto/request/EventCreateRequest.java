package classfit.example.classfit.event.dto.request;

import classfit.example.classfit.event.domain.Event;
import classfit.example.classfit.event.domain.EventRepeatType;
import classfit.example.classfit.event.domain.EventType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record EventCreateRequest(
    String name,
    EventType eventType,
    Long categoryId,
    LocalDateTime startDate,
    LocalDateTime endDate,
    boolean isAllDay,
    Optional<EventRepeatType> eventRepeatType,
    Optional<LocalDateTime> repeatEndDate,
    List<Long> memberIds,
    Optional<String> location,
    Optional<String> memo) {
    public LocalDateTime getEndDate() {
        return Event.getEndDate(eventType, startDate, endDate);
    }
}
