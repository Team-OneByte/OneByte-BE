package classfit.example.classfit.event.dto.request;

import classfit.example.classfit.event.domain.Event;
import classfit.example.classfit.event.domain.EventType;
import java.time.LocalDateTime;
import java.util.Optional;

public record EventModalRequest(
    String name,
    EventType eventType,
    long categoryId,
    LocalDateTime startDate,
    LocalDateTime endDate,
    boolean isAllDay
) {
    public LocalDateTime getEndDate() {
        return Event.getEndDate(eventType, startDate, endDate);
    }
}
