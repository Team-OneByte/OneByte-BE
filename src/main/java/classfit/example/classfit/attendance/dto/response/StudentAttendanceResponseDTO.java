package classfit.example.classfit.attendance.dto.response;

import java.util.List;

public record StudentAttendanceResponseDTO(
        Long id,
        String name,
        List<AttendanceResponseDTO> attendance) {

    public static StudentAttendanceResponseDTO of(final Long id, final String name, final List<AttendanceResponseDTO> attendance) {
        return new StudentAttendanceResponseDTO(id, name, attendance);
    }
}
