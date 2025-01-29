package classfit.example.classfit.attendance.dto.request;

import java.util.List;

public record StudentAttendanceUpdateRequest(
        Long studentId,
        List<AttendanceStatusUpdateRequest> attendance
) {
}