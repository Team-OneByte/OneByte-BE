package classfit.example.classfit.attendance.service;

import classfit.example.classfit.attendance.domain.Attendance;
import classfit.example.classfit.attendance.dto.response.AttendanceResponse;
import classfit.example.classfit.attendance.dto.response.StudentAttendanceResponse;
import classfit.example.classfit.attendance.repository.AttendanceRepository;
import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.student.domain.Student;
import java.util.Map;
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
    public List<StudentAttendanceResponse> getAttendanceData(
        int month,
        Long subClassId,
        Member member) {
        Long academyId = member.getAcademy().getId();
        List<Attendance> attendances = findAttendancesByAcademyAndSubClassAndMonth(academyId, subClassId, month);

        Map<ClassStudent, List<Attendance>> groupedAttendances = attendances.stream()
            .collect(Collectors.groupingBy(Attendance::getClassStudent));

        return groupedAttendances.entrySet().stream()
            .sorted(Comparator.comparing(entry -> entry.getKey().getStudent().getName()))
            .map(entry -> toStudentAttendanceResponse(entry.getKey().getStudent(), entry.getValue()))
            .collect(Collectors.toList());
    }

    private List<Attendance> findAttendancesByAcademyAndSubClassAndMonth(Long academyId, Long subClassId, int month) {
        List<Attendance> attendances;

        if (subClassId != null) {
            attendances = attendanceRepository.findByAcademyIdAndSubClassIdAndMonth(academyId, subClassId, month);
        } else {
            attendances = attendanceRepository.findByAcademyIdAndMonth(academyId, month);
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
        return AttendanceResponse.of(attendance.getId(), attendance.getDate(), attendance.getWeek(), attendance.getStatus().name());
    }
}
