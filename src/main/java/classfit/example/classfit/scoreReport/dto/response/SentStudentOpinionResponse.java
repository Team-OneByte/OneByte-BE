package classfit.example.classfit.scoreReport.dto.response;


import lombok.Builder;

@Builder
public record SentStudentOpinionResponse(
        Long reportId,
        Long studentId,
        String studentName,
        String studentOpinion
) {

    public static SentStudentOpinionResponse of(
            Long reportId,
            Long studentId,
            String studentName,
            String studentOpinion) {
        return SentStudentOpinionResponse.builder()
                .reportId(reportId)
                .studentId(studentId)
                .studentName(studentName)
                .studentOpinion(studentOpinion)
                .build();
    }
}
