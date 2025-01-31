package classfit.example.classfit.studentExam.dto.studentScoreRequest;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateStudentScoreRequest(
        Long studentId,

        @NotNull(message = "점수는 필수 입력값입니다.") Integer score,
        String evaluationDetail,

        boolean checkedStudent
) {
    public static UpdateStudentScoreRequest of(
            Long studentId,
            Integer score,
            Integer highestScore,
            String evaluationDetail,
            boolean checkedStudent
    ) {
        if (score > highestScore) {
            throw new ClassfitException(ErrorCode.SCORE_EXCEEDS_HIGHEST);
        }
        return UpdateStudentScoreRequest.builder()
                .studentId(studentId)
                .score(score)
                .evaluationDetail(evaluationDetail)
                .checkedStudent(checkedStudent)
                .build();
    }
}