package classfit.example.classfit.calendar.event.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record EventDragUpdate(
        @Schema(description = "이벤트 시작 날짜", required = true)
        LocalDateTime startDate,

        @Schema(description = "이벤트 종료 날짜", required = true)
        LocalDateTime endDate
) {
}