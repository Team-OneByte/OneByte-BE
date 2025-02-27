package classfit.example.classfit.scoreReport.dto.process;

import classfit.example.classfit.exam.domain.enumType.ExamPeriod;

import java.time.LocalDateTime;

public record ReportExam(
        Long examId,
        ExamPeriod examPeriod,
        String mainClassName,
        String subClassName,
        String examName,
        LocalDateTime createAt
) {
}
