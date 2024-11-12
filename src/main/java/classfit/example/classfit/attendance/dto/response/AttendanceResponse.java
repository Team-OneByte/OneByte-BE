package classfit.example.classfit.attendance.dto.response;

import java.time.LocalDate;

public record AttendanceResponse(
        Long id,
        LocalDate date,
        String status) {

    public static AttendanceResponse of(final Long id, final LocalDate date, final String status) {
        return new AttendanceResponse(id, date, status);
    }
}