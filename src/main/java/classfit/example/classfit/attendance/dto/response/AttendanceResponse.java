package classfit.example.classfit.attendance.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AttendanceResponse(
        Long id,
        LocalDate date,
        int week,
        String status
) {
    public static AttendanceResponse of(final Long id, final LocalDate date, final int week, final String status) {
        return AttendanceResponse.builder()
                .id(id)
                .date(date)
                .week(week)
                .status(status)
                .build();
    }
}