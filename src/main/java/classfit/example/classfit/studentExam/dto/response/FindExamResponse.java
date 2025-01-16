package classfit.example.classfit.studentExam.dto.response;

import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.ExamPeriod;
import classfit.example.classfit.studentExam.domain.Standard;
import classfit.example.classfit.member.domain.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public record FindExamResponse(
        Long examId,
        ExamPeriod examPeriod,
        Long memberId,
        String memberName,
        Standard standard,
        String mainClassName,
        String subClassName,
        String examName,
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdAt
) {

    public static FindExamResponse from(Exam exam, Member member) {
        return new FindExamResponse(
                exam.getId(),
                exam.getExamPeriod(),
                member.getId(),
                member.getName(),
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
