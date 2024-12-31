package classfit.example.classfit.event.dto.request;

import classfit.example.classfit.event.domain.EventType;
import classfit.example.classfit.event.domain.NotificationTime;
import java.time.LocalDateTime;

public record EventCreateRequest(
    String name,
    EventType eventType,
    Long categoryId,
    LocalDateTime startDate,
    LocalDateTime endDate,
    boolean isAllDay,
    boolean isRepeating,
    NotificationTime notificationTime,
    String location,
    String memo) {
}
