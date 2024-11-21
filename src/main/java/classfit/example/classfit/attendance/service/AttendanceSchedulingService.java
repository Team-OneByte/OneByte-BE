package classfit.example.classfit.attendance.service;

import classfit.example.classfit.attendance.domain.Attendance;
import classfit.example.classfit.attendance.domain.AttendanceStatus;
import classfit.example.classfit.attendance.repository.AttendanceRepository;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceSchedulingService {
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    // 매주 일요일 00:00시에 실행 - 이미 생성된 마지막 주의 다음 주 데이터 생성
    @Transactional
    @Scheduled(cron = "0 0 0 * * SUN")
    public void createWeeklyAttendance() {
        LocalDate lastGeneratedDate = attendanceRepository.findLastGeneratedDate()
            .orElse(LocalDate.now().with(DayOfWeek.MONDAY));
        LocalDate nextWeekStart = lastGeneratedDate.plusWeeks(1);
        generateAttendanceDataForWeeks(nextWeekStart);
    }

    private void generateAttendanceDataForWeeks(LocalDate startOfWeek) {
        List<Student> students = getAllStudents();

        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plusDays(i);
            for (Student student : students) {
                if (!attendanceRepository.existsByStudentAndDate(student, date)) {
                    Attendance attendance = Attendance.builder()
                        .date(date)
                        .status(AttendanceStatus.PRESENT)
                        .student(student)
                        .build();
                    attendanceRepository.save(attendance);
                }
            }
        }
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}
