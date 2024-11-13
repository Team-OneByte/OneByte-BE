package classfit.example.classfit.student.dto.response;

import classfit.example.classfit.common.Gender;
import classfit.example.classfit.domain.Student;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record StudentInfoResponse
    (
        Long studentId,
        String name,
        Gender gender,
        String studentNumber,
        String parentNumber,
        String grade,
        List<String> subClassList,
        String address,
        String remark,
        String counselingLog,
        boolean isStudent
    ) {

    public static StudentInfoResponse of(Student student, List<String> subClassList) {
        return StudentInfoResponse.builder()
            .studentId(student.getId())
            .name(student.getName())
            .gender(student.getGender())
            .studentNumber(student.getStudentNumber())
            .parentNumber(student.getParentNumber())
            .grade(student.getGrade())
            .subClassList(subClassList)
            .address(student.getAddress())
            .remark(student.getRemark())
            .counselingLog(student.getCounselingLog())
            .isStudent(student.isStudent())
            .build();
    }
}
