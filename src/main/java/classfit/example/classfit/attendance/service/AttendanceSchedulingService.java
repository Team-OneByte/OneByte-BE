package classfit.example.classfit.attendance.service;

import classfit.example.classfit.attendance.repository.AttendanceRepository;
import classfit.example.classfit.common.AttendanceStatus;
import classfit.example.classfit.domain.Attendance;
import classfit.example.classfit.domain.Student;
import classfit.example.classfit.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceSchedulingService {
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void createDailyAttendance() {
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
}
