package classfit.example.classfit.scoreReport.domain;

import classfit.example.classfit.scoreReport.dto.process.ReportExam;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
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
}

