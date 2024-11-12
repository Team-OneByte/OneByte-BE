package classfit.example.classfit.attendance.dto.request;

public record AttendanceStatusUpdateRequestDTO(
        Long attendanceId,
        String status) {
}