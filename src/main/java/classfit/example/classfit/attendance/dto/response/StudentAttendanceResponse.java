package classfit.example.classfit.attendance.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record StudentAttendanceResponse(
        Long id,
        String name,
        List<AttendanceResponse> attendance
) {
    public static StudentAttendanceResponse of(final Long id, final String name, final List<AttendanceResponse> attendance) {
        return StudentAttendanceResponse.builder()
                .id(id)
                .name(name)
                .attendance(attendance)
                .build();
    }
}
