package classfit.example.classfit.exam.dto.process;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record ExamClassStudent(

        Long studentId,

        String name,

        @Min(value = 0, message = "점수는 0 이상이어야 합니다.")
        Integer score,

        String evaluationDetail,

        boolean checkedStudent
) {
    public static ExamClassStudent of(
            Long studentId,
            String name,
            Integer score,
            Integer highestScore,
            String evaluationDetail,
            boolean checkedStudent
    ) {
        if (score > highestScore) {
            throw new ClassfitException(ErrorCode.SCORE_EXCEEDS_HIGHEST);
        }
        return ExamClassStudent.builder()
                .studentId(studentId)
                .name(name)
                .score(score)
                .evaluationDetail(evaluationDetail)
                .checkedStudent(checkedStudent)
                .build();
    }
}
