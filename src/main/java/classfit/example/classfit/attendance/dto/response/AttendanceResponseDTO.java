package classfit.example.classfit.attendance.dto.response;

import java.time.LocalDate;

public record AttendanceResponseDTO(
        Long id,
        LocalDate date,
        String status) {

    public static AttendanceResponseDTO of(final Long id, final LocalDate date, final String status) {
        return new AttendanceResponseDTO(id, date, status);
    }
}