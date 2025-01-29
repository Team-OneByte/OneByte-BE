package classfit.example.classfit.studentExam.dto.process;

import classfit.example.classfit.studentExam.domain.Standard;

public record ExamHistory(
        Long examId,
        String examName,
        Standard standard,
        String average,
        Integer score
) {
}
