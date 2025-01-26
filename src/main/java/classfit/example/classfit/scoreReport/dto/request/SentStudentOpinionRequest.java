package classfit.example.classfit.scoreReport.dto.request;

public record SentStudentOpinionRequest(
        Long reportId,
        Long studentId,
        String studentOpinion
) {

}
