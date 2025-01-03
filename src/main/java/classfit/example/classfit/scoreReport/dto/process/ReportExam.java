package classfit.example.classfit.scoreReport.dto.process;

import classfit.example.classfit.studentExam.domain.ExamPeriod;

public record ReportExam(Long examId, ExamPeriod examPeriod, String mainClassName,
                         String subClassName, String examName) {

}
