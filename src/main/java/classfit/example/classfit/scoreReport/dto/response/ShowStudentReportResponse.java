package classfit.example.classfit.scoreReport.dto.response;

import java.time.LocalDate;
import org.springframework.cglib.core.Local;

public record ShowStudentReportResponse(Long studentId, String studentName, Long mainClassId,
                                        Long subClassId, String reportName, LocalDate startDate,
                                        Local endDate //attendance list로 출결석,횟수,토탈횟수까지 보여주기
) {

}
