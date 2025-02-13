package classfit.example.classfit.exam.dto.exam.response;

import classfit.example.classfit.exam.domain.Exam;
import classfit.example.classfit.exam.domain.enumType.ExamPeriod;
import classfit.example.classfit.exam.domain.enumType.Standard;
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

    public static UpdateExamResponse from(Exam exam) {
        return UpdateExamResponse.builder()
                .examId(exam.getId())
                .mainClassName(exam.getMainClass().getMainClassName())
                .subClassName(exam.getSubClass().getSubClassName())
                .examDate(exam.getExamDate())
                .standard(exam.getStandard())
                .highestScore(exam.getHighestScore())
                .examPeriod(exam.getExamPeriod())
                .examName(exam.getExamName())
                .examRange(exam.getExamRange())
                .build();
    }

}
