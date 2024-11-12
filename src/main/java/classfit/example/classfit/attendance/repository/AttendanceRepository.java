package classfit.example.classfit.attendance.repository;

import classfit.example.classfit.domain.Attendance;
import classfit.example.classfit.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByStudentAndDate(Student student, LocalDate date);

    @Query("SELECT MAX(a.date) FROM Attendance a")
    Optional<LocalDate> findLastGeneratedDate();
}