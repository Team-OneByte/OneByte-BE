package classfit.example.classfit.scoreReport.dto.response;


public record SentStudentOpinionResponse(
        Long reportId,
        Long studentId,
        String studentName,
        String studentOpinion
) {

}
