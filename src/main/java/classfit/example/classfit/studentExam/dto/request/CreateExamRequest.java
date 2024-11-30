package classfit.example.classfit.studentExam.dto.request;

import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.ExamPeriod;
import classfit.example.classfit.studentExam.domain.Standard;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record CreateExamRequest( Long subClassId,
                                Long mainClassId,
                                @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate examDate,
                                Standard standard,
                                Integer highestScore, ExamPeriod examPeriod, String examName,
                                String range) {

    public Exam toEntity(SubClass subClass, MainClass mainClass) {
        return Exam.builder()
                .subClass(subClass)
                .mainClass(mainClass)
                .examName(examName)
                .examDate(examDate)
                .standard(standard)
                .examPeriod(examPeriod)
                .highestScore(highestScore)
                .examRange(range)
                .build();
    }

}
