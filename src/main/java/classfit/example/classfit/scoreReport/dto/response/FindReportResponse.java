package classfit.example.classfit.scoreReport.dto.response;

import classfit.example.classfit.scoreReport.domain.ScoreReport;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record FindReportResponse(
        Long studentReportId,
        Long studentId,
        String studentName,
        String reportName,
        String reportCreatedBy,
        LocalDate createAt
) {

    public static FindReportResponse from(ScoreReport report) {
        return FindReportResponse.builder()
                .studentReportId(report.getId())
                .studentId(report.getStudent().getId())
                .studentName(report.getStudent().getName())
                .reportName(report.getReportName())
                .reportCreatedBy(report.getReportCreatedBy())
                .createAt(report.getCreatedAt().toLocalDate())
                .build();
    }
}
