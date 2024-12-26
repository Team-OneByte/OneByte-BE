package classfit.example.classfit.scoreReport.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreReportRepository extends JpaRepository<ScoreReport,Long> {

}
