package classfit.example.classfit.scoreReport.dto.response;

import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.scoreReport.domain.ScoreReport;
import classfit.example.classfit.student.dto.StudentList;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record CreateReportResponse(
        boolean includeAverage,
        Long mainClassId,
        Long subClassId,
        List<StudentList> studentList,
        String reportName,
        LocalDate startDate,
        LocalDate endDate,
        List<Long> examIdList,
        String overallOpinion,
        String reportCreatedBy
) {
    public static CreateReportResponse of(List<StudentList> studentList, Long mainClassId, Long subClassId,
            String reportName, LocalDate startDate, LocalDate endDate,
            Member member,boolean includeAverage) {
        return new CreateReportResponse(
                includeAverage,
                mainClassId,
                subClassId,
                studentList,
                reportName,
                startDate,
                endDate,
                List.of(),
                null,
                member.getName()
        );
    }

}
