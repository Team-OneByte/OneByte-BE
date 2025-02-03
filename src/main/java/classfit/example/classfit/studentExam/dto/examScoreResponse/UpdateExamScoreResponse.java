package classfit.example.classfit.studentExam.dto.examScoreResponse;

import classfit.example.classfit.studentExam.domain.Standard;
import classfit.example.classfit.studentExam.dto.process.ExamStudent;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateExamScoreResponse(
        Standard standard,
        Integer highestScore,
        List<ExamStudent> examStudents
) {

    public static UpdateExamScoreResponse of(
            Standard standard,
            Integer highestScore) {
        return UpdateExamScoreResponse.builder()
                .standard(standard)
                .highestScore(highestScore)
                .build();
    }
}
