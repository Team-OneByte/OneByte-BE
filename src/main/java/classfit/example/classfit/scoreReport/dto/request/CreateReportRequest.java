package classfit.example.classfit.scoreReport.dto.request;

import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.scoreReport.domain.ScoreReport;
import classfit.example.classfit.student.domain.Student;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public record CreateReportRequest
    (
        Long mainClassId,
        Long subClassId,
        String reportName,
        Boolean includeAverage,
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
        @NotNull(message = "시험 리스트를 선택해주세요.") List<Long> examIdList,
        @NotNull(message = "종합 의견을 입력해주세요.")
        String overallOpinion
    ) {

    public ScoreReport toEntity(SubClass subClass, MainClass mainClass, Student student, Member member) {
        return ScoreReport.builder()
            .subClass(subClass)
            .mainClass(mainClass)
            .student(student)
            .reportName(reportName)
            .includeAverage(includeAverage)
            .overallOpinion(overallOpinion)
            .reportCreatedBy(member.getName())
            .build();
    }
}
