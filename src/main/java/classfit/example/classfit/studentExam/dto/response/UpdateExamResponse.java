package classfit.example.classfit.studentExam.dto.response;

import classfit.example.classfit.studentExam.domain.ExamPeriod;
import classfit.example.classfit.studentExam.domain.Standard;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record UpdateExamResponse(
        Long examId,
        String mainClassName,
        String subClassName,
        LocalDate examDate,
        Standard standard,
        Integer highestScore,
        ExamPeriod examPeriod,
        String examName,
        List<String> examRange
) {

    public static UpdateExamResponse of(
            Long examId,
            String mainClassName,
            String examName) {
        return UpdateExamResponse.builder()
                .examId(examId)
                .mainClassName(mainClassName)
                .examName(examName)
                .build();
    }

}
