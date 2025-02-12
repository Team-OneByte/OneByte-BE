package classfit.example.classfit.exam.repository;

import classfit.example.classfit.exam.domain.Exam;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long>, ExamRepositoryCustom {
    @Query("SELECT e FROM Exam e " +
            "JOIN e.mainClass mc " +
            "JOIN mc.academy a " +
            "JOIN a.members m " +
            "WHERE a.id = :academyId " +
            "AND m.name = :memberName")
    List<Exam> findByAcademyIdAndMemberName(@Param("academyId") Long academyId,
            @Param("memberName") String memberName);

    @Query("SELECT e FROM Exam e " +
            "JOIN e.mainClass mc " +
            "JOIN mc.academy a " +
            "WHERE a.id = :academyId " +
            "AND e.examName = :examName")
    List<Exam> findByAcademyIdAndExamName(@Param("academyId") Long academyId,
            @Param("examName") String examName);


    @Query("SELECT e FROM Exam e WHERE e.mainClass.academy.id = :academyId ORDER BY e.id ASC")
    List<Exam> findAllByAcademyId(@Param("academyId") Long academyId);

    @Query("SELECT e FROM Exam e " +
            "JOIN e.mainClass mc " +
            "JOIN mc.academy a " +
            "WHERE a.id = :academyId " +
            "AND mc.id = :mainClassId " +
            "AND e.subClass.id = :subClassId")
    List<Exam> findByAcademyIdAndMainClassIdAndSubClassId(@Param("academyId") Long academyId,
            @Param("mainClassId") Long mainClassId,
            @Param("subClassId") Long subClassId);


}
