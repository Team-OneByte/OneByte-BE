package classfit.example.classfit.exam.dto.exam.response;

import lombok.Builder;

@Builder
public record FindExamStudentResponse(
        Long studentId,
        String studentName
) {

}
