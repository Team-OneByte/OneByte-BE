package classfit.example.classfit.attendance.dto.response;

import java.time.LocalDate;

public record AttendanceResponse(
    Long id,
    LocalDate date,
    int week,
    String status
) {
    public static AttendanceResponse of(
        final Long id,
        final LocalDate date,
        final int week,
        final String status
    ) {
        return new AttendanceResponse(id, date, week, status);
    }
}