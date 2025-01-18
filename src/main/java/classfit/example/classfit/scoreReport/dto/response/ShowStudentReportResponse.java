package classfit.example.classfit.scoreReport.dto.response;

import classfit.example.classfit.attendance.dto.process.AttendanceInfo;
import classfit.example.classfit.studentExam.dto.process.ExamHistory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.cglib.core.Local;

public record ShowStudentReportResponse(Long studentId, String studentName, String mainClassName,
                                        String subClassName, String reportName, LocalDate startDate,
                                        LocalDate endDate, List<AttendanceInfo> attendanceInfoList,
                                        Integer totalAttendanceCount,Boolean includeAverage,
                                        List<ExamHistory> examHistoryList, String overallOpinion,
                                        String studentOpinion
) {

}
