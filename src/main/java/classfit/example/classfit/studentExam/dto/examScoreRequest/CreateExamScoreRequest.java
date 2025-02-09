package classfit.example.classfit.studentExam.dto.examScoreRequest;

import classfit.example.classfit.studentExam.domain.StandardStatus;
import jakarta.validation.constraints.Min;

public record CreateExamScoreRequest(
        Long studentId,
        Long examId,
        @Min(value = 0, message ="학생의 점수는 0이상 이어야 합니다.")Integer score,
        StandardStatus standardStatus ) {

}
