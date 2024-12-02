package classfit.example.classfit.studentExam.dto.response;

import classfit.example.classfit.student.domain.Student;

public record ShowExamClassStudentResponse(Long studentId, String studentName, Integer highestScore) {
    public static ShowExamClassStudentResponse from(Student student, Integer highestScore) {
        return new ShowExamClassStudentResponse(
                student.getId(),
                student.getName(),
                highestScore
        );
    }
}
