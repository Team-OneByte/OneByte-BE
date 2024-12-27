package classfit.example.classfit.scoreReport.dto.response;

import classfit.example.classfit.student.dto.StudentList;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record CreateReportResponse(Long mainClassId, Long subClassId, List<StudentList> studentList,
                                   String reportName, LocalDate startDate, LocalDate endDate,
                                   List<Long> examIdList, String overallOpinion) {

}
