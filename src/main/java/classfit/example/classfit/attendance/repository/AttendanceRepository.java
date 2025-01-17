package classfit.example.classfit.attendance.repository;

import classfit.example.classfit.attendance.domain.Attendance;
import classfit.example.classfit.attendance.domain.AttendanceStatus;
import classfit.example.classfit.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByStudentAndDate(Student student, LocalDate date);

    @Query("SELECT MAX(a.date) FROM Attendance a")
    Optional<LocalDate> findLastGeneratedDate();

    @Query("SELECT a FROM Attendance a WHERE a.student IN " +
        "(SELECT cs.student FROM ClassStudent cs WHERE cs.subClass.id = :subClassId AND cs.subClass.mainClass.academy.id =:academyId) " +
        "AND FUNCTION('MONTH', a.date) = :month")
    List<Attendance> findByAcademyIdAndSubClassIdAndMonth(
        @Param("academyId") Long academyId,
        @Param("subClassId") Long subClassId,
        @Param("month") int month);

    @Query("SELECT a FROM Attendance a WHERE a.student IN " +
        "(SELECT cs.student FROM ClassStudent cs WHERE cs.subClass.mainClass.academy.id =:academyId) " +
        "AND FUNCTION('MONTH', a.date) = :month")
    List<Attendance> findByAcademyIdAndMonth(
        @Param("academyId") Long academyId,
        @Param("month") int month);

    @Query("SELECT a FROM Attendance a JOIN a.classStudent cs " +
        "WHERE cs.subClass.id = :subClassId " +
        "AND cs.subClass.mainClass.academy.id = :academyId " +
        "AND a.date BETWEEN :startDate AND :endDate")
    List<Attendance> findByDateBetweenAndSubClassIdAndAcademyId(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("subClassId") Long subClassId,
        @Param("academyId") Long academyId);

    @Query("SELECT a FROM Attendance a JOIN a.classStudent cs " +
        "WHERE cs.subClass.id = :subClassId " +
        "AND cs.subClass.mainClass.academy.id = :academyId " +
        "AND a.date = :date " +
        "AND a.status = :status")
    List<Attendance> findByDateAndSubClassIdAndStatusAndAcademyId(
        @Param("date") LocalDate date,
        @Param("subClassId") Long subClassId,
        @Param("status") AttendanceStatus status,
        @Param("academyId") Long academyId);

    List<Attendance> findByStudentIdAndDateBetween(Long studentId, LocalDate startDate,
        LocalDate endDate);

    @Query("SELECT a FROM Attendance a JOIN a.classStudent cs " +
        "WHERE a.student.id = :studentId " +
        "AND FUNCTION('MONTH', a.date) = :month " +
        "AND a.status = :status " +
        "AND cs.subClass.mainClass.academy.id = :academyId ")
    List<Attendance> findByAcademyIdAndStudentIdAndMonthAndStatus(
        @Param("studentId") Long studentId,
        @Param("month") int month,
        @Param("status") AttendanceStatus status,
        @Param("academyId") Long academyId);
}