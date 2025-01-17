package classfit.example.classfit.studentExam.dto.response;

import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.ExamPeriod;
import classfit.example.classfit.studentExam.domain.Standard;
import classfit.example.classfit.member.domain.Member;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record FindExamResponse(
        Long examId,
        ExamPeriod examPeriod,
        Long memberId,
        Standard standard,
        String mainClassName,
        String subClassName,
        String examName,
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate examDate,
        String createdByName
        ) {


    public static FindExamResponse from(Exam exam, Member findMember) {
        Member createdByMember = findMember.getAcademy().getMembers().stream()
                .filter(member -> member.getId().equals(exam.getCreatedBy()))
                .findFirst()
                .orElse(null);

        return new FindExamResponse(
                exam.getId(),
                exam.getExamPeriod(),
                findMember.getId(),
                exam.getStandard(),
                exam.getMainClass().getMainClassName(),
                exam.getSubClass().getSubClassName(),
                exam.getExamName(),
                exam.getExamDate(),
                createdByMember != null ? createdByMember.getName() : "선생님"
        );
    }
}
