package classfit.example.classfit.studentExam.dto.response;

import classfit.example.classfit.studentExam.domain.ExamPeriod;
import classfit.example.classfit.studentExam.domain.Standard;
import classfit.example.classfit.studentExam.dto.process.ExamClassStudent;
import java.time.LocalDate;
import java.util.List;

public record ShowExamDetailResponse(ExamPeriod examPeriod, String examName, LocalDate examDate,
                                     String mainClassName, String subClassName,
                                     Integer lowestScore, Integer perfectScore, Long average,
                                     Standard standard, List<ExamClassStudent> examClassStudents) {

}
