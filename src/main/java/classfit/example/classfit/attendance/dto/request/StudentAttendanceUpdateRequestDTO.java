package classfit.example.classfit.attendance.dto.request;

import java.util.List;

public record StudentAttendanceUpdateRequestDTO(
        Long studentId,
        List<AttendanceStatusUpdateRequestDTO> attendance) {
}