package classfit.example.classfit.attendance.repository;

import classfit.example.classfit.domain.Attendance;
import classfit.example.classfit.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByStudentAndDate(Student student, LocalDate date);
}