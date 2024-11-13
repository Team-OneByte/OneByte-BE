package classfit.example.classfit.attendance.dto.request;

public record AttendanceStatusUpdateRequest(
        Long attendanceId,
        String status) {
}