package classfit.example.classfit.studentExam.dto.request;

import classfit.example.classfit.studentExam.domain.ExamPeriod;
import classfit.example.classfit.studentExam.domain.Standard;
import java.time.LocalDate;

public record UpdateExamRequest(LocalDate examDate, Standard standard, Integer highestScore,
                                ExamPeriod examPeriod, String examName, String examRange) {

}
