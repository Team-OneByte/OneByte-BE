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
public interface StudentExamScoreRepository extends JpaRepository<StudentExamScore, Long> {

    Optional<StudentExamScore> findByExamAndStudentId(Exam exam, Long studentId);

    Optional<StudentExamScore> findByStudentAndExamId(Student student, Long examId);

    List<StudentExamScore> findByExam(Exam exam);

    List<StudentExamScore> findByScoreReport(ScoreReport scoreReport);

    long countByExamAndScore(Exam exam, int score);

    @Modifying
    @Transactional
    @Query("DELETE FROM StudentExamScore ses WHERE ses.scoreReport.id = :reportId")
    void deleteByReportId(@Param("reportId") Long reportId);
}
