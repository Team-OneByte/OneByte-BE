package classfit.example.classfit.studentExam.dto.examRequest;

public record FindExamRequest(
        Long mainClassId,
        Long subClassId,
        String memberName,
        String examName
) {
}
