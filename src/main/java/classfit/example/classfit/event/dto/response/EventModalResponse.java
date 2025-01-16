package classfit.example.classfit.event.dto.response;

import classfit.example.classfit.event.domain.EventRepeatType;
import classfit.example.classfit.event.domain.EventType;
import classfit.example.classfit.memberCalendar.domain.CalendarType;
import java.time.LocalDateTime;

public record EventModalResponse
    (
        Long id,
        String name,
        CalendarType calendarType,
        EventType eventType,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long categoryId,
        EventRepeatType eventRepeatType,
        LocalDateTime repeatEndDate,
        boolean isAllDay
        ) {
    public static EventModalResponse of(final Long id, final String name, final CalendarType calendarType, final EventType eventType, final LocalDateTime startDate, final LocalDateTime endDate, final Long categoryId, final EventRepeatType eventRepeatType, final LocalDateTime repeatEndDate, final boolean isAllDay) {
        return new EventModalResponse(id, name, calendarType, eventType, startDate, endDate, categoryId, eventRepeatType, repeatEndDate, isAllDay);
    }
}
