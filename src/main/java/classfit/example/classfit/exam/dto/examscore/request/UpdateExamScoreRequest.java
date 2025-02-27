package classfit.example.classfit.exam.dto.examscore.request;

import classfit.example.classfit.exam.domain.enumType.StandardStatus;
import jakarta.validation.constraints.Min;


public record UpdateExamScoreRequest(
        Long studentId,
        @Min(value = 0, message ="시험의 최고점수는 0이상 이어야 합니다.")
        Integer score,
        StandardStatus standardStatus,
        String evaluationDetail,
        boolean checkedStudent
) {

}