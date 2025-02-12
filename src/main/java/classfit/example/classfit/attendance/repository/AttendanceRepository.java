package classfit.example.classfit.attendance.repository;

import classfit.example.classfit.attendance.domain.Attendance;
import classfit.example.classfit.attendance.domain.enumType.AttendanceStatus;
import classfit.example.classfit.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Attendance a " +
        "JOIN a.enrollment cs WHERE cs.student = :student AND a.date = :date")
    boolean existsByStudentAndDate(
        @Param("student") Student student,
        @Param("date") LocalDate date
    );

    @Query("SELECT MAX(a.date) FROM Attendance a")
    Optional<LocalDate> findLastGeneratedDate();

    @Query("SELECT a FROM Attendance a WHERE a.enrollment.student IN " +
        "(SELECT cs.student FROM Enrollment cs WHERE cs.subClass.id = :subClassId AND cs.subClass.mainClass.academy.id =:academyId) " +
        "AND FUNCTION('MONTH', a.date) = :month")
    List<Attendance> findByAcademyIdAndSubClassIdAndMonth(
        @Param("academyId") Long academyId,
        @Param("subClassId") Long subClassId,
        @Param("month") int month);

    @Query("SELECT a FROM Attendance a WHERE a.enrollment.student IN " +
        "(SELECT cs.student FROM Enrollment cs WHERE cs.subClass.mainClass.academy.id =:academyId) " +
        "AND FUNCTION('MONTH', a.date) = :month")
    List<Attendance> findByAcademyIdAndMonth(
        @Param("academyId") Long academyId,
        @Param("month") int month);

    @Query("SELECT a FROM Attendance a JOIN a.enrollment cs " +
        "WHERE cs.subClass.id = :subClassId " +
        "AND cs.subClass.mainClass.academy.id = :academyId " +
        "AND a.date BETWEEN :startDate AND :endDate")
    List<Attendance> findByDateBetweenAndSubClassIdAndAcademyId(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("subClassId") Long subClassId,
        @Param("academyId") Long academyId);

    @Query("SELECT a FROM Attendance a JOIN a.enrollment cs " +
        "WHERE cs.subClass.id = :subClassId " +
        "AND cs.subClass.mainClass.academy.id = :academyId " +
        "AND a.date = :date " +
        "AND a.status = :status")
    List<Attendance> findByDateAndSubClassIdAndStatusAndAcademyId(
        @Param("date") LocalDate date,
        @Param("subClassId") Long subClassId,
        @Param("status") AttendanceStatus status,
        @Param("academyId") Long academyId);

    @Query("SELECT a FROM Attendance a JOIN a.enrollment cs " +
        "WHERE cs.student.id = :studentId " +
        "AND a.date BETWEEN :startDate AND :endDate")
    List<Attendance> findByStudentIdAndDateBetween(
        @Param("studentId") Long studentId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM Attendance a JOIN a.enrollment cs " +
        "WHERE a.enrollment.student.id = :studentId " +
        "AND FUNCTION('MONTH', a.date) = :month " +
        "AND a.status = :status " +
        "AND cs.subClass.mainClass.academy.id = :academyId ")
    List<Attendance> findByAcademyIdAndStudentIdAndMonthAndStatus(
        @Param("studentId") Long studentId,
        @Param("month") int month,
        @Param("status") AttendanceStatus status,
        @Param("academyId") Long academyId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Attendance a WHERE a.enrollment.student.id = :studentId")
    void deleteByStudentId(@Param("studentId") Long studentId);
}