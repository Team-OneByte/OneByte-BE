package classfit.example.classfit.studentExam.dto.examRequest;

import classfit.example.classfit.studentExam.domain.ExamPeriod;
import classfit.example.classfit.studentExam.domain.Standard;

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
