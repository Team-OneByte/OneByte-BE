package classfit.example.classfit.studentExam.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentExamScoreRepository extends JpaRepository<StudentExamScore, Long> {

    Optional<StudentExamScore> findByExamAndStudentId(Exam exam, Long studentId);

    List<StudentExamScore> findByStudentIdAndExamIdIn(Long studentId, List<Long> examIdList);

    List<StudentExamScore> findByExam(Exam exam);
}
