package classfit.example.classfit.attendance.dto.response;

import java.time.LocalDate;

public record AttendanceResponseDTO(
        LocalDate date,
        String status) {

    public static AttendanceResponseDTO of(final LocalDate date, final String status) {
        return new AttendanceResponseDTO(date, status);
    }
}