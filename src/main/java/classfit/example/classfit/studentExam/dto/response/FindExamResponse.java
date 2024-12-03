package classfit.example.classfit.studentExam.dto.response;


import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.Standard;
import java.time.LocalDate;

public record FindExamResponse(Long examId, Long memberId, Standard standard, String mainClassName,
                               String subClassName, String examName, LocalDate examDate // 일단 시험날짜로

) {

    public static FindExamResponse from(Exam exam) {
        return new FindExamResponse(
                exam.getId(),
                exam.getMainClass().getMember().getId(),
                exam.getStandard(),
                exam.getMainClass().getMainClassName(),
                exam.getSubClass().getSubClassName(),
                exam.getExamName(),
                exam.getExamDate()
        );
    }
}
