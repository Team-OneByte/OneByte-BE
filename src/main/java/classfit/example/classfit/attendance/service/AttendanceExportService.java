package classfit.example.classfit.attendance.service;

import classfit.example.classfit.attendance.dto.response.AttendanceResponse;
import classfit.example.classfit.attendance.dto.response.StudentAttendanceResponse;
import classfit.example.classfit.attendance.repository.AttendanceRepository;
import classfit.example.classfit.domain.Attendance;
import classfit.example.classfit.domain.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceExportService {
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public List<StudentAttendanceResponse> getAttendanceData(int month, Long subClassId) {
        List<Attendance> attendances = findAttendancesBySubClassIdAndMonth(subClassId, month);

        return attendances.stream()
                .collect(Collectors.groupingBy(Attendance::getStudent))
                .entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getName()))
                .map(entry -> toStudentAttendanceResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private List<Attendance> findAttendancesBySubClassIdAndMonth(Long subClassId, int month) {
        List<Attendance> attendances;

        if (subClassId != null) {
            attendances = attendanceRepository.findBySubClassIdAndMonth(subClassId, month);
        } else {
            attendances = attendanceRepository.findByMonth(month);
        }
        return attendances;
    }

    private StudentAttendanceResponse toStudentAttendanceResponse(Student student, List<Attendance> attendances) {
        List<AttendanceResponse> attendanceResponses = attendances.stream()
                .sorted(Comparator.comparing(Attendance::getDate))
                .map(this::toAttendanceResponse)
                .collect(Collectors.toList());
        return new StudentAttendanceResponse(student.getId(), student.getName(), attendanceResponses);
    }

    private AttendanceResponse toAttendanceResponse(Attendance attendance) {
        return AttendanceResponse.of(attendance.getId(), attendance.getDate(), attendance.getStatus().name());
    }
}
