package classfit.example.classfit.student.dto.response;

import classfit.example.classfit.common.Gender;
import classfit.example.classfit.domain.Student;
import lombok.Builder;

@Builder
public record StudentResponse
    (
        Long studentId,
        String name,
        Gender gender,
        String studentNumber,
        String parentNumber,
        String address,
        String remark,
        String counselingLog,
        boolean isStudent
    ) {

    public static StudentResponse from(Student student) {
        return StudentResponse.builder()
            .studentId(student.getId())
            .name(student.getName())
            .gender(student.getGender())
            .studentNumber(student.getStudentNumber())
            .parentNumber(student.getParentNumber())
            .address(student.getAddress())
            .remark(student.getRemark())
            .counselingLog(student.getCounselingLog())
            .isStudent(student.isStudent())
            .build();
    }
}
