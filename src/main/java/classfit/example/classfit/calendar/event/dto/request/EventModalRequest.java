package classfit.example.classfit.calendar.event.dto.request;

import classfit.example.classfit.calendar.event.domain.Event;
import classfit.example.classfit.calendar.event.domain.enumType.EventRepeatType;
import classfit.example.classfit.calendar.event.domain.enumType.EventType;
import classfit.example.classfit.calendar.memberCalendar.domain.enumType.CalendarType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Optional;

public record EventModalRequest(
        @Schema(description = "이벤트 이름", required = true)
        String name,

        @Schema(description = "이벤트 유형", required = true)
        EventType eventType,

        @Schema(description = "캘린더 유형", required = true)
        CalendarType calendarType,

        @Schema(description = "카테고리 ID", required = false)
        Optional<Long> categoryId,

        @Schema(description = "이벤트 시작 날짜", required = true)
        LocalDateTime startDate,

        @Schema(description = "이벤트 종료 날짜", required = true)
        LocalDateTime endDate,

        @Schema(description = "종일 이벤트 여부", required = true)
        boolean isAllDay,

        @Schema(description = "이벤트 반복 유형", required = true)
        EventRepeatType eventRepeatType,

        @Schema(description = "반복 종료 날짜", required = false)
        Optional<LocalDateTime> repeatEndDate
) {
    public LocalDateTime getEndDate() {
        return Event.getEndDate(eventType, startDate, endDate);
    }
}
