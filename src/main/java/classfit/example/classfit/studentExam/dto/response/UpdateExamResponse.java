package classfit.example.classfit.studentExam.dto.response;

import classfit.example.classfit.studentExam.domain.ExamPeriod;
import classfit.example.classfit.studentExam.domain.Standard;
import java.time.LocalDate;

public record UpdateExamResponse(Long examId, String mainClassName, String subClassName,
                                 LocalDate examDate, Standard standard, Integer highestScore,
                                 ExamPeriod examPeriod, String examName, String examRange) {

}
