package classfit.example.classfit.exam.dto.exam.request;

import classfit.example.classfit.exam.domain.enumType.ExamPeriod;
import classfit.example.classfit.exam.domain.enumType.Standard;

import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

public record UpdateExamRequest(
        LocalDate examDate,
        Standard standard,
        @Min(value = 0, message ="시험의 최고점수는 0이상 이어야 합니다.")
        Integer highestScore,
        ExamPeriod examPeriod,
        String examName,
        List<String> examRange
) {
}
