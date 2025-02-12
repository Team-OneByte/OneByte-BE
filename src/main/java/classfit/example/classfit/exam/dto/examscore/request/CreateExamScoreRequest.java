package classfit.example.classfit.exam.dto.examscore.request;

import classfit.example.classfit.exam.domain.Exam;
import classfit.example.classfit.exam.domain.enumType.Standard;
import classfit.example.classfit.exam.domain.enumType.StandardStatus;

public record CreateExamScoreRequest(
        Long studentId,
        Long examId,
        Integer score,
        StandardStatus standardStatus,
        String evaluationDetail,
        boolean checkedStudent) {
    public boolean isScoreRequired(Exam exam) {
        return exam.getStandard() != Standard.PF && exam.getStandard() != Standard.EVALUATION;
    }


}
