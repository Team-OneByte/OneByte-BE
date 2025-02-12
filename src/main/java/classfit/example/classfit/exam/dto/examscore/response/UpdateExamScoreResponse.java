package classfit.example.classfit.exam.dto.examscore.response;

import classfit.example.classfit.exam.dto.process.ExamStudent;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateExamScoreResponse(
        Integer highestScore,
        List<ExamStudent> examStudents
) {

    public static UpdateExamScoreResponse of(
            Integer highestScore, List<ExamStudent> examStudents) {
        return UpdateExamScoreResponse.builder()
                .highestScore(highestScore)
                .examStudents(examStudents)
                .build();
    }
}
