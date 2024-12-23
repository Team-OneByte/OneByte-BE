package classfit.example.classfit.studentExam.dto.response;


import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.Standard;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
// TODO memberName과 examPeriod 나오도록 수정필요
public record FindExamResponse(Long examId, Long memberId, Standard standard, String mainClassName,
                               String subClassName, String examName, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdAt

) {

    public static FindExamResponse from(Exam exam) {
        return new FindExamResponse(
                exam.getId(),
                exam.getMainClass().getMember().getId(),
                exam.getStandard(),
                exam.getMainClass().getMainClassName(),
                exam.getSubClass().getSubClassName(),
                exam.getExamName(),
                convertToLocalDate(exam.getCreatedAt())
        );
    }
private static LocalDate convertToLocalDate(LocalDateTime dateTime) {
    return dateTime.toLocalDate();
}
}
