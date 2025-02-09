package classfit.example.classfit.studentExam.dto.examScoreRequest;

import classfit.example.classfit.studentExam.domain.StandardStatus;
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