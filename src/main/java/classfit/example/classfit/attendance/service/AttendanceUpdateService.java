package classfit.example.classfit.attendance.service;

import classfit.example.classfit.attendance.dto.request.AttendanceStatusUpdateRequestDTO;
import classfit.example.classfit.attendance.dto.request.StudentAttendanceUpdateRequestDTO;
import classfit.example.classfit.attendance.dto.response.AttendanceResponseDTO;
import classfit.example.classfit.attendance.dto.response.StudentAttendanceResponseDTO;
import classfit.example.classfit.attendance.repository.AttendanceRepository;
import classfit.example.classfit.domain.Attendance;
import classfit.example.classfit.domain.Student;
import classfit.example.classfit.exception.ClassfitException;
import classfit.example.classfit.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static classfit.example.classfit.exception.ClassfitException.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceUpdateService {
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    public List<StudentAttendanceResponseDTO> updateStudentAttendances(List<StudentAttendanceUpdateRequestDTO> students) {
        return students.stream()
                .map(this::updateStudentAttendance)
                .toList();
    }

    private StudentAttendanceResponseDTO updateStudentAttendance(StudentAttendanceUpdateRequestDTO studentRequest) {
        Student student = findStudentById(studentRequest.studentId());
        List<AttendanceResponseDTO> attendanceResponses = studentRequest.attendance().stream()
                .map(this::updateAttendanceStatus)
                .toList();

        return StudentAttendanceResponseDTO.of(student.getId(), student.getName(), attendanceResponses);
    }

    private AttendanceResponseDTO updateAttendanceStatus(AttendanceStatusUpdateRequestDTO attendanceRequest) {
        Attendance attendance = findAttendanceById(attendanceRequest.attendanceId());
        attendance.updateStatus(attendanceRequest.status());
        attendanceRepository.save(attendance);

        return AttendanceResponseDTO.of(attendance.getId(), attendance.getDate(), attendance.getStatus().name());
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