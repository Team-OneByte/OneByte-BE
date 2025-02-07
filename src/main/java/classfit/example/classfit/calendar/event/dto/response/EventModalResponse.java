package classfit.example.classfit.calendar.event.dto.response;

import classfit.example.classfit.calendar.event.domain.enumType.EventRepeatType;
import classfit.example.classfit.calendar.event.domain.enumType.EventType;
import classfit.example.classfit.calendar.memberCalendar.domain.enumType.CalendarType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EventModalResponse(
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
        return EventModalResponse.builder()
                .id(id)
                .name(name)
                .calendarType(calendarType)
                .eventType(eventType)
                .startDate(startDate)
                .endDate(endDate)
                .categoryId(categoryId)
                .eventRepeatType(eventRepeatType)
                .repeatEndDate(repeatEndDate)
                .isAllDay(isAllDay)
                .build();
    }
}
