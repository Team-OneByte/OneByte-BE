package classfit.example.classfit.studentExam.dto.examResponse;

import lombok.Builder;

@Builder
public record FindExamStudentResponse(
        Long studentId,
        String studentName
) {

}
