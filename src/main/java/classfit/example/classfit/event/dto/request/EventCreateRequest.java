package classfit.example.classfit.event.dto.request;

import java.time.LocalDateTime;

public record EventCreateRequest(
    String name,
    Long categoryId,
    LocalDateTime startDate,
    LocalDateTime endDate,
    boolean isAllDay,
    boolean isRepeating,
    String notificationTime,
    String location,
    String memo) {
}
