package classfit.example.classfit.studentExam.dto.response;

import classfit.example.classfit.studentExam.domain.Standard;
import classfit.example.classfit.studentExam.dto.process.ExamStudent;
import java.util.List;
import lombok.Builder;

@Builder
public record UpdateStudentScoreResponse(
        Standard standard,
        Integer highestScore,
        List<ExamStudent> examStudents
) {

    public static UpdateStudentScoreResponse of(
            Standard standard,
            Integer highestScore) {
        return UpdateStudentScoreResponse.builder()
                .standard(standard)
                .highestScore(highestScore)
                .build();
    }
}
