package classfit.example.classfit.studentExam.dto.examScoreRequest;

import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.StandardStatus;
import classfit.example.classfit.studentExam.domain.ExamScore;
import jakarta.validation.constraints.Min;

public record CreateExamScoreRequest(
        Long studentId,
        Long examId,
        @Min(value = 0, message ="학생의 점수는 0이상 이어야 합니다.")Integer score,
        StandardStatus standardStatus ) {
    public ExamScore toEntity(
            Student student,
            Exam exam) {
        return ExamScore.builder()
                .student(student)
                .exam(exam)
                .score(score)
                .standardStatus(standardStatus)
                .evaluationDetail(null)
                .build();
    }

}
