package classfit.example.classfit.scoreReport.dto.response;

import classfit.example.classfit.scoreReport.domain.ScoreReport;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record FindReportResponse(
        Long studentReportId,
        Long studentId,
        String studentName,
        String reportName,
        String reportCreatedBy,
        LocalDate createAt
) {

    public static FindReportResponse from(final ScoreReport report) {
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
