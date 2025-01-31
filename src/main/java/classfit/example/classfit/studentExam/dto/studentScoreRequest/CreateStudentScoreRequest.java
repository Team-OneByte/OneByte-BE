package classfit.example.classfit.studentExam.dto.studentScoreRequest;

import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.StandardStatus;
import classfit.example.classfit.studentExam.domain.StudentExamScore;

public record CreateStudentScoreRequest(
        Long studentId,
        Long examId,
        Integer score,
        StandardStatus standardStatus ) {
    public StudentExamScore toEntity(Student student, Exam exam) {
        return new StudentExamScore(student, exam, score, standardStatus, null,null);
    }
}
