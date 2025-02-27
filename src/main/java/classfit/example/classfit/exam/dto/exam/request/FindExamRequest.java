package classfit.example.classfit.exam.dto.exam.request;

public record FindExamRequest(
        Long mainClassId,
        Long subClassId,
        String memberName,
        String examName
) {
}
