package classfit.example.classfit.attendance.dto.process;

import classfit.example.classfit.attendance.domain.enumType.AttendanceStatus;

public record AttendanceInfo(
        AttendanceStatus attendanceStatus,
        Integer attendanceCount
) {
}
