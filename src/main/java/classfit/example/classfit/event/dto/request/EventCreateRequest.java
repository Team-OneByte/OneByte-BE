package classfit.example.classfit.event.dto.request;

import classfit.example.classfit.event.domain.Event;
import classfit.example.classfit.event.domain.EventRepeatType;
import classfit.example.classfit.event.domain.EventType;
import classfit.example.classfit.memberCalendar.domain.CalendarType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record EventCreateRequest(
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
    Optional<LocalDateTime> repeatEndDate,
    @Schema(description = "참여자 ID 리스트", required = true)
    List<Long> memberIds,
    @Schema(description = "위치", required = false)
    Optional<String> location,
    @Schema(description = "메모", required = false)
    Optional<String> memo) {
    public LocalDateTime getEndDate() {
        return Event.getEndDate(eventType, startDate, endDate);
    }
}
