package classfit.example.classfit.student.dto.response;

import classfit.example.classfit.common.Gender;
import classfit.example.classfit.domain.Student;
import classfit.example.classfit.domain.SubClass;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record StudentResponse
    (
        Long studentId,
        String name,
        Gender gender,
        String studentNumber,
        String parentNumber,
        String grade,
        List<SubClass> subClassList,
        String address,
        String remark,
        String counselingLog,
        boolean isStudent
    ) {

    public static StudentResponse from(Student student) {
        return StudentResponse.builder()
            .studentId(student.getId())
            .name(student.getName())
            .studentNumber(student.getStudentNumber())
            .grade(student.getGrade())
            .isStudent(student.isStudent())
            .build();
    }
}
