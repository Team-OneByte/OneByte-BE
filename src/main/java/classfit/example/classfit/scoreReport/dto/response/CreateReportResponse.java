package classfit.example.classfit.scoreReport.dto.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record CreateReportResponse(Long reportId, Long mainClassId, Long subClassId,
                                   String reportName, LocalDate startDate, LocalDate endDate,
                                   List<Long> examIdList, String overallOpinion) {

}
