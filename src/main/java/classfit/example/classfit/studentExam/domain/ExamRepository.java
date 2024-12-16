package classfit.example.classfit.studentExam.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findByMainClassMemberName(String name);

    List<Exam> findByExamName(String examName);

    @Query("SELECT e FROM Exam e WHERE e.mainClass.member.academy.id = :academyId ORDER BY e.id ASC")
    List<Exam> findAllByAcademyId(@Param("academyId") Long academyId);


}
