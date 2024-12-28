package classfit.example.classfit.scoreReport.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentReportRepository extends JpaRepository<StudentReport, Long> {

    @Query("""
                SELECT sr
                FROM StudentReport sr
                JOIN sr.scoreReport scoreReport
                WHERE scoreReport.mainClass.id = :mainClassId
                  AND scoreReport.subClass.id = :subClassId
                  AND sr.id IN (
                      SELECT MIN(sr2.id)
                      FROM StudentReport sr2
                      JOIN sr2.examIdList exam
                      WHERE sr2.student = sr.student
                      GROUP BY sr2.student
                  )
            """)
    List<StudentReport> findFirstReportByStudent(@Param("mainClassId") Long mainClassId,
            @Param("subClassId") Long subClassId);
}
