package classfit.example.classfit.attendance.controller;

import classfit.example.classfit.attendance.controller.docs.AttendanceStatisticsControllerDocs;
import classfit.example.classfit.attendance.domain.enumType.AttendanceStatus;
import classfit.example.classfit.attendance.dto.response.StatisticsDateResponse;
import classfit.example.classfit.attendance.dto.response.StatisticsMemberResponse;
import classfit.example.classfit.attendance.service.AttendanceStatisticsService;
import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/home/statistics")
@RequiredArgsConstructor
public class AttendanceStatisticsController implements AttendanceStatisticsControllerDocs {

    private final AttendanceStatisticsService attendanceStatisticsService;

    @Override
    @GetMapping("/date")
    public CustomApiResponse<List<StatisticsDateResponse>> getAttendanceStatisticsByDate(
            @AuthMember Member member,
            @RequestParam(value = "startDate") LocalDate startDate,
            @RequestParam(value = "endDate") LocalDate endDate,
            @RequestParam(value = "subClassId") Long subClassId
    ) {
        List<StatisticsDateResponse> statisticsDate = attendanceStatisticsService.getAttendanceStatisticsByDate(startDate, endDate, subClassId, member);
        return CustomApiResponse.success(statisticsDate, 200, "날짜별 출결 통계 조회 성공");
    }

    @Override
    @GetMapping("/date/details")
    public CustomApiResponse<List<String>> getAttendanceDetailsByDateAndStatus(
            @AuthMember Member member,
            @RequestParam(value = "date") LocalDate date,
            @RequestParam(value = "subClassId") Long subClassId,
            @RequestParam(value = "status") AttendanceStatus status
    ) {
        List<String> studentDetails = attendanceStatisticsService.getAttendanceDetailsByDateAndStatus(date, subClassId, status, member);
        return CustomApiResponse.success(studentDetails, 200, "출결 세부 정보 조회 성공");
    }

    @Override
    @GetMapping("/member")
    public CustomApiResponse<List<StatisticsMemberResponse>> getAttendanceStatisticsByMember(
            @AuthMember Member member,
            @RequestParam(value = "startDate") LocalDate startDate,
            @RequestParam(value = "endDate") LocalDate endDate
    ) {
        List<StatisticsMemberResponse> statisticsDate = attendanceStatisticsService.getAttendanceStatisticsByMember(startDate, endDate, member);
        return CustomApiResponse.success(statisticsDate, 200, "구성원별 출결 통계 조회 성공");
    }

    @Override
    @GetMapping("/member/details")
    public CustomApiResponse<List<String>> getAttendanceDetailsByMemberAndStatus(
            @AuthMember Member member,
            @RequestParam(value = "studentId") Long studentId,
            @RequestParam(value = "month") int month,
            @RequestParam(value = "status") AttendanceStatus status
    ) {
        List<String> studentDetails = attendanceStatisticsService.getAttendanceDetailsByMemberAndStatus(studentId, month, status, member);
        return CustomApiResponse.success(studentDetails, 200, "구성원별 통계 세부 조회 성공");
    }
}
