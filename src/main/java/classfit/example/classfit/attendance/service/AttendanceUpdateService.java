package classfit.example.classfit.attendance.service;

import classfit.example.classfit.attendance.dto.request.AttendanceStatusUpdateRequest;
import classfit.example.classfit.attendance.dto.request.StudentAttendanceUpdateRequest;
import classfit.example.classfit.attendance.dto.response.AttendanceResponse;
import classfit.example.classfit.attendance.dto.response.StudentAttendanceResponse;
import classfit.example.classfit.attendance.repository.AttendanceRepository;
import classfit.example.classfit.domain.Attendance;
import classfit.example.classfit.domain.Student;
import classfit.example.classfit.exception.ClassfitException;
import classfit.example.classfit.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static classfit.example.classfit.exception.ClassfitException.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceUpdateService {
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public List<StudentAttendanceResponse> updateStudentAttendances(List<StudentAttendanceUpdateRequest> students) {
        return students.stream()
                .map(this::updateStudentAttendance)
                .toList();
    }

    private StudentAttendanceResponse updateStudentAttendance(StudentAttendanceUpdateRequest studentRequest) {
        Student student = findStudentById(studentRequest.studentId());
        List<AttendanceResponse> attendanceResponses = studentRequest.attendance().stream()
                .map(this::updateAttendanceStatus)
                .toList();

        return StudentAttendanceResponse.of(student.getId(), student.getName(), attendanceResponses);
    }

    private AttendanceResponse updateAttendanceStatus(AttendanceStatusUpdateRequest attendanceRequest) {
        Attendance attendance = findAttendanceById(attendanceRequest.attendanceId());
        attendance.updateStatus(attendanceRequest.status());
        attendanceRepository.save(attendance);

        return AttendanceResponse.of(attendance.getId(), attendance.getDate(), attendance.getStatus().name());
    }

    private Student findStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ClassfitException(STUDENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private Attendance findAttendanceById(Long attendanceId) {
        return attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ClassfitException(ATTENDANCE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}