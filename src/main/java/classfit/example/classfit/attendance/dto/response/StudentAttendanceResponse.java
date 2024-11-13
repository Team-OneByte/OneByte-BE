package classfit.example.classfit.attendance.dto.response;

import java.util.List;

public record StudentAttendanceResponse(
        Long id,
        String name,
        List<AttendanceResponse> attendance) {

    public static StudentAttendanceResponse of(final Long id, final String name, final List<AttendanceResponse> attendance) {
        return new StudentAttendanceResponse(id, name, attendance);
    }
}
