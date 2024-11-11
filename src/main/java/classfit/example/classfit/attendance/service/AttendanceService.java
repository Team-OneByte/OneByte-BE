package classfit.example.classfit.attendance.service;

import classfit.example.classfit.attendance.repository.AttendanceRepository;
import classfit.example.classfit.common.AttendanceStatus;
import classfit.example.classfit.attendance.service.util.DateRangeUtil;
import classfit.example.classfit.domain.Attendance;
import classfit.example.classfit.domain.Student;
import classfit.example.classfit.student.repository.StudentRepository;
import classfit.example.classfit.attendance.dto.response.AttendanceResponseDTO;
import classfit.example.classfit.attendance.dto.response.StudentAttendanceResponseDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    private void createDailyAttendance() {
        List<Student> students = getAllStudents();
        LocalDate today = LocalDate.now();

        students.forEach(student -> {
            if (!attendanceRepository.existsByStudentAndDate(student, today)) {
                Attendance attendance = Attendance.builder()
                        .date(today)
                        .status(AttendanceStatus.PRESENT)
                        .student(student)
                        .build();
                attendanceRepository.save(attendance);
            }
        });
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<LocalDate> getWeeklyAttendanceRange() {
        LocalDate today = LocalDate.now();
        return DateRangeUtil.getWeekRange(today);
    }

    public List<StudentAttendanceResponseDTO> getStudentAttendance(List<Student> students, List<LocalDate> weekRange) {
        return students.stream()
                .map(student -> mapToStudentAttendanceDTO(student, weekRange))
                .collect(Collectors.toList());
    }

    private StudentAttendanceResponseDTO mapToStudentAttendanceDTO(Student student, List<LocalDate> weekRange) {
        List<AttendanceResponseDTO> attendanceDTOs = student.getAttendances().stream()
                .filter(attendance -> weekRange.contains(attendance.getDate()))
                .map(attendance -> AttendanceResponseDTO.of(attendance.getDate(), attendance.getStatus().name()))
                .collect(Collectors.toList());
        return new StudentAttendanceResponseDTO(student.getId(), student.getName(), attendanceDTOs);
    }
}