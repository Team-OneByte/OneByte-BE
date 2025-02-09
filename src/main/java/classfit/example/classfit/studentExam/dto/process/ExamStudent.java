package classfit.example.classfit.studentExam.dto.process;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.studentExam.domain.StandardStatus;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record ExamStudent(
        Long studentId,
        String name,
        @Min(value = 0, message = "점수는 0 이상이어야 합니다.") Integer score,
        StandardStatus standardStatus,
        String evaluationDetail,
        boolean checkedStudent
) {
    public static ExamStudent of(
            Long studentId,
            String name,
            Integer score,
            Integer highestScore,
            StandardStatus standardStatus,
            String evaluationDetail,
            boolean checkedStudent
    ) {
        if (score > highestScore) {
            throw new ClassfitException(ErrorCode.SCORE_EXCEEDS_HIGHEST);
        }
        return ExamStudent.builder()
                .studentId(studentId)
                .name(name)
                .score(score)
                .standardStatus(standardStatus)
                .evaluationDetail(evaluationDetail)
                .checkedStudent(checkedStudent)
                .build();
    }
}
