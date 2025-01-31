package classfit.example.classfit.studentExam.dto.studentScoreRequest;

import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.StandardStatus;
import classfit.example.classfit.studentExam.domain.StudentExamScore;
import jakarta.validation.constraints.Min;

public record CreateStudentScoreRequest(
        Long studentId,
        Long examId,
        @Min(value = 0, message ="학생의 점수는 0이상 이어야 합니다.")Integer score,
        StandardStatus standardStatus ) {
    public StudentExamScore toEntity(Student student, Exam exam) {
        return new StudentExamScore(student, exam, score, standardStatus, null,null);
    }
}
