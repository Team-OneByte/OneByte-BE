package classfit.example.classfit.exam.dto.process;

import classfit.example.classfit.exam.domain.enumType.Standard;

public record ExamHistory(
        Long examId,
        String examName,
        Standard standard,
        String average,
        Integer score
) {
}
