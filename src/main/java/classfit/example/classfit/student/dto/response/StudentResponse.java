package classfit.example.classfit.student.dto.response;

import classfit.example.classfit.student.domain.Student;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record StudentResponse(
    Long studentId,

    String name,

    String studentNumber,

    boolean isStudent
) {
    public static StudentResponse from(Student student) {
        return StudentResponse.builder()
            .studentId(student.getId())
            .name(student.getName())
            .studentNumber(student.getStudentNumber())
            .isStudent(student.isStudent())
            .build();
    }
}
