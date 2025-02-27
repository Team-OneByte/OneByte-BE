package classfit.example.classfit.scoreReport.dto.response;

import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.scoreReport.dto.process.StudentList;
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

    public static CreateReportResponse of(
            List<StudentList> studentList,
            Long mainClassId,
            Long subClassId,
            String reportName,
            LocalDate startDate,
            LocalDate endDate,
            Member member,
            boolean includeAverage) {
        return CreateReportResponse.builder()
                .studentList(studentList)
                .mainClassId(mainClassId)
                .subClassId(subClassId)
                .reportName(reportName)
                .startDate(startDate)
                .endDate(endDate)
                .reportCreatedBy(member.getName())
                .includeAverage(includeAverage)
                .build();
    }

}
