package classfit.example.classfit.studentExam.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    @Query("SELECT e FROM Exam e " +
            "JOIN e.mainClass mc " +
            "JOIN mc.academy a " +
            "JOIN a.members m " +
            "WHERE a.name = :academyName " +
            "AND m.name = :memberName")
    List<Exam> findByAcademyAndMemberName(@Param("academyName") String academyName,
            @Param("memberName") String memberName);


    List<Exam> findByExamName(String examName);

    @Query("SELECT e FROM Exam e WHERE e.mainClass.academy.id = :academyId ORDER BY e.id ASC")
    List<Exam> findAllByAcademyId(@Param("academyId") Long academyId);

    List<Exam> findByMainClassIdAndSubClassId(Long mainClassId, Long subClassId);
}
