package classfit.example.classfit.event.dto.request;

import classfit.example.classfit.event.domain.Event;
import classfit.example.classfit.event.domain.EventRepeatType;
import classfit.example.classfit.event.domain.EventType;
import java.time.LocalDateTime;
import java.util.List;

public record EventCreateRequest(
    String name,
    EventType eventType,
    Long categoryId,
    LocalDateTime startDate,
    LocalDateTime endDate,
    boolean isAllDay,
    EventRepeatType eventRepeatType,
    LocalDateTime repeatEndDate,
    List<Long> memberIds,
    String location,
    String memo) {
    public LocalDateTime getEndDate() {
        return Event.getEndDate(eventType, startDate, endDate);
    }
}
