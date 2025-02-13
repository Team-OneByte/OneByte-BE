package classfit.example.classfit.exam.dto.exam.response;

import classfit.example.classfit.student.domain.Student;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record FindExamStudentResponse(
        Long studentId,
        String studentName
) {

    public static FindExamStudentResponse of(Student student) {
        return FindExamStudentResponse.builder()
                .studentId(student.getId())
                .studentName(student.getName())
                .build();
    }

    public static List<FindExamStudentResponse> from(List<Student> students) {
        return students.stream()
                .map(FindExamStudentResponse::of)
                .collect(Collectors.toList());
    }

}
