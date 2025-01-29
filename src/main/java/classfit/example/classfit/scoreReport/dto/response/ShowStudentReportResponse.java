package classfit.example.classfit.scoreReport.dto.response;

import classfit.example.classfit.attendance.dto.process.AttendanceInfo;
import classfit.example.classfit.studentExam.dto.process.ExamHistory;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record ShowStudentReportResponse(
        Long studentId,
        String studentName,
        String mainClassName,
        String subClassName,
        String reportName,
        LocalDate startDate,
        LocalDate endDate,
        List<AttendanceInfo> attendanceInfoList,
        Integer totalAttendanceCount,
        Boolean includeAverage,
        List<ExamHistory> examHistoryList,
        String overallOpinion,
        String studentOpinion
) {

    public static ShowStudentReportResponse of(
            Long studentId,
            String studentName,
            String mainClassName,
            String subClassName,
            String reportName,
            LocalDate startDate,
            LocalDate endDate,
            List<AttendanceInfo> attendanceInfoList,
            Integer totalAttendanceCount,
            Boolean includeAverage,
            List<ExamHistory> examHistoryList,
            String overallOpinion,
            String studentOpinion) {
        return ShowStudentReportResponse.builder()
                .studentId(studentId)
                .studentName(studentName)
                .mainClassName(mainClassName)
                .subClassName(subClassName)
                .reportName(reportName)
                .startDate(startDate)
                .endDate(endDate)
                .attendanceInfoList(attendanceInfoList)
                .totalAttendanceCount(totalAttendanceCount)
                .includeAverage(includeAverage)
                .examHistoryList(examHistoryList)
                .overallOpinion(overallOpinion)
                .studentOpinion(studentOpinion)
                .build();
    }
}
