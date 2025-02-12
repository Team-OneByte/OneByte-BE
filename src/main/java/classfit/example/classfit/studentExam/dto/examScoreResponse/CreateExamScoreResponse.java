package classfit.example.classfit.studentExam.dto.examScoreResponse;

import classfit.example.classfit.studentExam.domain.ExamScore;
import classfit.example.classfit.studentExam.domain.StandardStatus;
import java.util.List;
import lombok.Builder;

//TODO ExamStudent로 바꿔도 되지 않나
@Builder
public record CreateExamScoreResponse(
        Long examScoreId,
        Long examId,
        Long studentId,
        Integer score,
        StandardStatus standardStatus
) {
    public static CreateExamScoreResponse from(ExamScore examScore) {
        return CreateExamScoreResponse.builder()
                .examScoreId(examScore.getId())
                .examId(examScore.getExam().getId())
                .studentId(examScore.getStudent().getId())
                .score(examScore.getScore())
                .standardStatus(examScore.getStandardStatus())
                .build();
    }
    public static List<CreateExamScoreResponse> from(List<ExamScore> examScores) {
        return examScores.stream()
                .map(CreateExamScoreResponse::from)
                .toList();
    }
}
