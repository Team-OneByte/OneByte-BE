package classfit.example.classfit.scoreReport.domain;

import classfit.example.classfit.scoreReport.dto.process.ReportExam;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreReportRepository extends JpaRepository<ScoreReport,Long> {
    @Query("SELECT new classfit.example.classfit.scoreReport.dto.process.ReportExam(e.id, e.examPeriod, e.mainClass.mainClassName,e.subClass.subClassName,e.examName) " +
            "FROM Exam e " +
            "WHERE FUNCTION('DATE', e.createdAt) BETWEEN :startDate AND :endDate " +
            "ORDER BY e.createdAt ASC")
    List<ReportExam> findExamsByCreatedAtBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
    SELECT sr
    FROM ScoreReport sr
    WHERE sr.mainClass.id = :mainClassId
      AND sr.subClass.id = :subClassId
      AND sr.id IN (
          SELECT MIN(sr2.id)
          FROM ScoreReport sr2
          WHERE sr2.student.id = sr.student.id
          GROUP BY sr2.student.id
      )
""")
    List<ScoreReport> findFirstReportByStudent(@Param("mainClassId") Long mainClassId,
            @Param("subClassId") Long subClassId);

    @Modifying
    @Query("""
        DELETE FROM ScoreReport sr
        WHERE sr.id IN :reportIds
    """)
    void deleteByIds(@Param("reportIds") List<Long> reportIds);
    @Query("SELECT sr FROM ScoreReport sr WHERE sr.student.id = :studentId")
    List<ScoreReport> findByStudentId(@Param("studentId") Long studentId);
    // 특정 studentId, mainClassId, subClassId에 해당하는 ScoreReport id 목록 조회
    @Query("""
        SELECT sr.id
        FROM ScoreReport sr
        WHERE sr.student.id = :studentId
          AND sr.mainClass.id = :mainClassId
          AND sr.subClass.id = :subClassId
    """)
    List<Long> findReportIdsByStudentIdAndClass(
            @Param("studentId") Long studentId,
            @Param("mainClassId") Long mainClassId,
            @Param("subClassId") Long subClassId
    );

    // studentReportId로 ScoreReport 조회 (mainClassId와 subClassId를 얻기 위해)
    @Query("SELECT sr FROM ScoreReport sr WHERE sr.id = :studentReportId")
    Optional<ScoreReport> findById(@Param("studentReportId") Long studentReportId);

}

