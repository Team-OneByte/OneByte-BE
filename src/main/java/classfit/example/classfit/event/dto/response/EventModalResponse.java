package classfit.example.classfit.event.dto.response;

import classfit.example.classfit.event.domain.EventRepeatType;
import classfit.example.classfit.event.domain.EventType;
import java.time.LocalDateTime;

public record EventModalResponse
    (
        Long id,
        String name,
        EventType eventType,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long categoryId,
        EventRepeatType eventRepeatType,
        LocalDateTime repeatEndDate,
        boolean isAllDay
        ) {
    public static EventModalResponse of(final Long id, final String name, final EventType eventType, final LocalDateTime startDate, final LocalDateTime endDate, final Long categoryId, final EventRepeatType eventRepeatType, final LocalDateTime repeatEndDate, final boolean isAllDay) {
        return new EventModalResponse(id, name, eventType, startDate, endDate, categoryId, eventRepeatType, repeatEndDate, isAllDay);
    }
}
