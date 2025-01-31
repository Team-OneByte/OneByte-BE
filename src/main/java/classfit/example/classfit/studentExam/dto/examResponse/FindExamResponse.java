package classfit.example.classfit.studentExam.dto.examResponse;

import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.ExamPeriod;
import classfit.example.classfit.studentExam.domain.Standard;
import classfit.example.classfit.member.domain.Member;
import java.time.LocalDate;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
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

    public static FindExamResponse from(
            Exam exam,
            Member findMember) {
        Member createdByMember = findMember.getAcademy().getMembers().stream()
                .filter(member -> member.getId().equals(exam.getCreatedBy()))
                .findFirst()
                .orElse(null);

        return FindExamResponse.builder()
                .examId(exam.getId())
                .examPeriod(exam.getExamPeriod())
                .memberId(findMember.getId())
                .standard(exam.getStandard())
                .mainClassName(exam.getMainClass().getMainClassName())
                .subClassName(exam.getSubClass().getSubClassName())
                .examName(exam.getExamName())
                .examDate(exam.getExamDate())
                .createdByName(createdByMember != null ? createdByMember.getName() : "선생님")
                .build();
    }
}
