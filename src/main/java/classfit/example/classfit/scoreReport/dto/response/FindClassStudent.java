package classfit.example.classfit.scoreReport.dto.response;

import classfit.example.classfit.student.domain.Student;
import lombok.Builder;

@Builder
public record FindClassStudent(
        Long studentId,
        String studentName
) {

    public static FindClassStudent from(Student student) {
        return FindClassStudent.builder()
                .studentId(student.getId())
                .studentName(student.getName())
                .build();
    }
}
