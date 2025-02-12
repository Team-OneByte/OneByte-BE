package classfit.example.classfit.studentExam.dto.examScoreRequest;

import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.Standard;
import classfit.example.classfit.studentExam.domain.StandardStatus;

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
