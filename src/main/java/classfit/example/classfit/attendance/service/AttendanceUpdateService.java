package classfit.example.classfit.attendance.service;

import classfit.example.classfit.attendance.domain.Attendance;
import classfit.example.classfit.attendance.dto.request.AttendanceStatusUpdateRequest;
import classfit.example.classfit.attendance.dto.request.StudentAttendanceUpdateRequest;
import classfit.example.classfit.attendance.dto.response.AttendanceResponse;
import classfit.example.classfit.attendance.dto.response.StudentAttendanceResponse;
import classfit.example.classfit.attendance.repository.AttendanceRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceUpdateService {
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public List<StudentAttendanceResponse> updateStudentAttendances(
        List<StudentAttendanceUpdateRequest> students,
        Member member) {
        Long academyId = member.getAcademy().getId();

        return students.stream()
            .map(studentRequest -> updateStudentAttendance(studentRequest, academyId))
            .toList();
    }

    private StudentAttendanceResponse updateStudentAttendance(
        StudentAttendanceUpdateRequest studentRequest,
        Long academyId) {
        Student student = findStudentByIdAndAcademy(studentRequest.studentId(), academyId);
        List<AttendanceResponse> attendanceResponses = studentRequest.attendance().stream()
            .map(this::updateAttendanceStatus)
            .toList();

        return StudentAttendanceResponse.of(student.getId(), student.getName(), attendanceResponses);
    }

    private AttendanceResponse updateAttendanceStatus(AttendanceStatusUpdateRequest attendanceRequest) {
        Attendance attendance = findAttendanceById(attendanceRequest.attendanceId());
        attendance.updateStatus(attendanceRequest.status());
        attendanceRepository.save(attendance);
        return AttendanceResponse.of(attendance.getId(), attendance.getDate(), attendance.getWeek(), attendance.getStatus().name());
    }

    private Student findStudentByIdAndAcademy(Long studentId, Long academyId) {
        return studentRepository.findByIdAndAcademyId(studentId, academyId)
            .orElseThrow(() -> new ClassfitException(ErrorCode.STUDENT_NOT_FOUND));
    }

    private Attendance findAttendanceById(Long attendanceId) {
        return attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new ClassfitException(ErrorCode.ATTENDANCE_NOT_FOUND));
    }
}