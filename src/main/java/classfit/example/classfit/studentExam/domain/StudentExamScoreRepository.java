package classfit.example.classfit.studentExam.domain;

import classfit.example.classfit.scoreReport.domain.ScoreReport;
import classfit.example.classfit.student.domain.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentExamScoreRepository extends JpaRepository<StudentExamScore, Long> {

    @Query("SELECT ses FROM StudentExamScore ses " +
            "JOIN ses.exam e " +
            "JOIN e.mainClass mc " +
            "JOIN mc.academy a " +
            "WHERE a.id = :academyId " +
            "AND e = :exam " +
            "AND ses.student.id = :studentId")
    Optional<StudentExamScore> findByExamAndStudentIdAndAcademyId(@Param("academyId") Long academyId,
            @Param("exam") Exam exam,
            @Param("studentId") Long studentId);


    Optional<StudentExamScore> findByStudentAndExamId(Student student, Long examId);

    @Query("SELECT ses FROM StudentExamScore ses " +
            "JOIN ses.exam e " +
            "JOIN e.mainClass mc " +
            "JOIN mc.academy a " +
            "WHERE a.id = :academyId " +
            "AND ses.exam = :exam")
    List<StudentExamScore> findByAcademyIdAndExam(@Param("academyId") Long academyId,
            @Param("exam") Exam exam);

    @Query("SELECT ses FROM StudentExamScore ses " +
            "JOIN ses.exam e " +
            "JOIN e.mainClass mc " +
            "JOIN mc.academy a " +
            "WHERE a.id = :academyId " +
            "AND ses.exam = :exam")
    List<StudentExamScore> findAllByAcademyIdAndExam(@Param("academyId") Long academyId,@Param("exam") Exam exam);


    List<StudentExamScore> findByScoreReport(ScoreReport scoreReport);

    long countByExamAndScore(Exam exam, int score);
    void deleteByScoreReport_Id(Long scoreReportId);

}
