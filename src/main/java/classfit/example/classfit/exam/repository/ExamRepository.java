package classfit.example.classfit.exam.repository;

import classfit.example.classfit.exam.domain.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long>, ExamRepositoryCustom {


}
