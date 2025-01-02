package classfit.example.classfit.studentExam.domain;

import classfit.example.classfit.scoreReport.domain.ScoreReport;
import classfit.example.classfit.student.domain.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentExamScoreRepository extends JpaRepository<StudentExamScore, Long> {

    Optional<StudentExamScore> findByExamAndStudentId(Exam exam, Long studentId);

    Optional<StudentExamScore> findByStudentAndExamId(Student student, Long examId);

    List<StudentExamScore> findByExam(Exam exam);

    List<StudentExamScore> findByScoreReport(ScoreReport scoreReport);

}
