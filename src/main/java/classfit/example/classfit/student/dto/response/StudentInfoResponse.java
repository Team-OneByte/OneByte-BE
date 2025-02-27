package classfit.example.classfit.student.dto.response;

import classfit.example.classfit.student.domain.enumType.GenderType;
import classfit.example.classfit.student.domain.Student;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record StudentInfoResponse(
        Long studentId,

        String name,

        GenderType genderType,

        LocalDate birth,

        String studentNumber,

        String parentNumber,

        String grade,

        List<String> subClassList,

        String address,

        String remark,

        String counselingLog,

        boolean isStudent
) {
    public static StudentInfoResponse of(final Student student, final List<String> subClassList) {
        return StudentInfoResponse.builder()
                .studentId(student.getId())
                .name(student.getName())
                .genderType(student.getGenderType())
                .birth(student.getBirth())
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
