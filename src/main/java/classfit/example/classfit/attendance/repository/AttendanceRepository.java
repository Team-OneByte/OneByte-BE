package classfit.example.classfit.attendance.repository;

import classfit.example.classfit.attendance.domain.Attendance;
import classfit.example.classfit.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByStudentAndDate(Student student, LocalDate date);

    @Query("SELECT MAX(a.date) FROM Attendance a")
    Optional<LocalDate> findLastGeneratedDate();

    @Query("SELECT a FROM Attendance a WHERE a.student IN (SELECT cs.student FROM ClassStudent cs WHERE cs.subClass.id = :subClassId) AND FUNCTION('MONTH', a.date) = :month")
    List<Attendance> findBySubClassIdAndMonth(Long subClassId, int month);

    @Query("SELECT a FROM Attendance a WHERE FUNCTION('MONTH', a.date) = :month")
    List<Attendance> findByMonth(int month);

    @Query("SELECT a FROM Attendance a JOIN a.classStudent cs WHERE cs.subClass.id = :subClassId AND a.date BETWEEN :startDate AND :endDate")
    List<Attendance> findByDateBetweenAndSubClassId(LocalDate startDate, LocalDate endDate,
        Long subClassId);

    List<Attendance> findByStudentIdAndDateBetween(Long studentId, LocalDate startDate,
        LocalDate endDate);
}