package classfit.example.classfit.studentExam.dto.response;

import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.ExamPeriod;
import classfit.example.classfit.studentExam.domain.Standard;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
public record CreateExamResponse(Long examId, Long subClassId, String subClassName,
                                 Long mainClassId, String mainClassName,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate examDate,
                                 Standard standard,
                                 Integer highestScore, ExamPeriod examPeriod, String examName,
                                 List<String> range) {

    public static CreateExamResponse from(Exam exam) {
        return CreateExamResponse.builder()
                .examId(exam.getId())
                .subClassId(exam.getSubClass().getId())
                .subClassName(exam.getSubClass().getSubClassName())
                .mainClassId(exam.getMainClass().getId())
                .mainClassName(exam.getMainClass().getMainClassName())
                .examDate(exam.getExamDate())
                .standard(exam.getStandard())
                .highestScore(exam.getHighestScore())
                .examPeriod(exam.getExamPeriod())
                .examName(exam.getExamName())
                .range(exam.getExamRange() != null && !exam.getExamRange().isBlank()
                        ? List.of(exam.getExamRange().split(",\\s*"))
                        : List.of())
                .build();
    }
}
