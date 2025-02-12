package classfit.example.classfit.studentExam.domain;

import classfit.example.classfit.scoreReport.domain.ScoreReport;
import classfit.example.classfit.student.domain.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface StudentExamScoreRepository extends JpaRepository<ExamScore, Long> {

    @Query("SELECT ses FROM ExamScore ses " +
            "JOIN ses.exam e " +
            "JOIN e.mainClass mc " +
            "JOIN mc.academy a " +
            "WHERE a.id = :academyId " +
            "AND e = :exam " +
            "AND ses.student.id = :studentId")
    Optional<ExamScore> findByExamAndStudentIdAndAcademyId(@Param("academyId") Long academyId,
            @Param("exam") Exam exam,
            @Param("studentId") Long studentId);


    Optional<ExamScore> findByStudentAndExamId(Student student, Long examId);

    @Query("SELECT DISTINCT s FROM Exam e " +
            "JOIN e.subClass sc " +
            "JOIN sc.enrollments cs " +
            "JOIN cs.student s " +
            "WHERE sc.mainClass.academy.id = :academyId " +
            "AND e.id = :examId")
    List<Student> findStudentsByExamIdAndAcademyId(@Param("academyId") Long academyId,
            @Param("examId") Long examId);


    @Query("SELECT ses FROM ExamScore ses " +
            "JOIN ses.exam e " +
            "JOIN e.mainClass mc " +
            "JOIN mc.academy a " +
            "WHERE a.id = :academyId " +
            "AND ses.exam = :exam")
    List<ExamScore> findAllByAcademyIdAndExam(@Param("academyId") Long academyId,@Param("exam") Exam exam);


    List<ExamScore> findByScoreReport(ScoreReport scoreReport);

    long countByExamAndScore(Exam exam, int score);
    void deleteByScoreReport_Id(Long scoreReportId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ExamScore ses WHERE ses.scoreReport.id = :reportId")
    void deleteByReportId(@Param("reportId") Long reportId);
}
