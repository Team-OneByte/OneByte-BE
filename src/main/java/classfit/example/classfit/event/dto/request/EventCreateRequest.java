package classfit.example.classfit.event.dto.request;

import classfit.example.classfit.event.domain.Event;
import classfit.example.classfit.event.domain.EventType;
import classfit.example.classfit.event.domain.NotificationTime;
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
    boolean isRepeating,
    NotificationTime notificationTime,
    List<Long> memberIds,
    String location,
    String memo) {
    public LocalDateTime getEndDate() {
        return Event.getEndDate(eventType, startDate, endDate);
    }
}
