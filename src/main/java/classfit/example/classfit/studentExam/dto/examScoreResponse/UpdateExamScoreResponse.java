package classfit.example.classfit.studentExam.dto.examScoreResponse;

import classfit.example.classfit.studentExam.domain.Standard;
import classfit.example.classfit.studentExam.domain.StandardStatus;
import classfit.example.classfit.studentExam.dto.process.ExamStudent;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateExamScoreResponse(
        Integer highestScore,
        List<ExamStudent> examStudents
) {

    public static UpdateExamScoreResponse of(
            Integer highestScore) {
        return UpdateExamScoreResponse.builder()
                .highestScore(highestScore)
                .build();
    }
}
