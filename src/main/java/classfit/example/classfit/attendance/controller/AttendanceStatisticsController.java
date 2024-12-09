package classfit.example.classfit.attendance.controller;

import classfit.example.classfit.attendance.domain.AttendanceStatus;
import classfit.example.classfit.attendance.dto.response.StatisticsDateResponse;
import classfit.example.classfit.attendance.dto.response.StatisticsMemberResponse;
import classfit.example.classfit.attendance.service.AttendanceStatisticsService;
import classfit.example.classfit.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/home/statistics")
@RequiredArgsConstructor
@Tag(name = "출결 통계 컨트롤러", description = "출결 통계 관련 API입니다.")
public class AttendanceStatisticsController {
    private final AttendanceStatisticsService attendanceStatisticsService;

    @GetMapping("/date")
    @Operation(summary = "클래스 날짜별 통계", description = "날짜별 출결 통계 조회 시 사용되는 api 입니다.")
    public ApiResponse<List<StatisticsDateResponse>> getAttendanceStaticsByDate(
        @Parameter(description = "조회 시작 기간")
        @RequestParam(value = "startDate") LocalDate startDate,
        @Parameter(description = "조회 끝 기간")
        @RequestParam(value = "endDate") LocalDate endDate,
        @Parameter(description = "조회하려는 서브 클래스 ID")
        @RequestParam(value = "subClassId") Long subClassId) {

        List<StatisticsDateResponse> statisticsDate = attendanceStatisticsService.getAttendanceStatisticsByDate(startDate, endDate, subClassId);
        return ApiResponse.success(statisticsDate, 200, "SUCCESS");
    }

    @GetMapping("/date/details")
    @Operation(summary = "클래스 날짜별 통계 세부 조회", description = "날짜별 출결 통계에서 세부 정보 조회 시 사용되는 api 입니다.")
    public ApiResponse<List<String>> getAttendanceDetailsByDateAndStatus(
        @Parameter(description = "조회 날짜")
        @RequestParam(value = "date") LocalDate date,
        @Parameter(description = "조회하려는 서브 클래스 ID")
        @RequestParam(value = "subClassId") Long subClassId,
        @Parameter(description = "조회하려는 출결 상태")
        @RequestParam(value = "status") AttendanceStatus status
    ) {
        List<String> studentDetails = attendanceStatisticsService.getAttendanceDetailsByDateAndStatus(date, subClassId, status);
        return ApiResponse.success(studentDetails, 200, "SUCCESS");
    }

    @GetMapping("/member")
    @Operation(summary = "클래스 구성원별 통계", description = "구성원별 출결 통계 조회 시 사용되는 api 입니다.")
    public ApiResponse getAttendanceStaticsByMember(
        @Parameter(description = "조회 시작 기간")
        @RequestParam(value = "startDate") LocalDate startDate,
        @Parameter(description = "조회 끝 기간")
        @RequestParam(value = "endDate") LocalDate endDate) {

        List<StatisticsMemberResponse> statisticsDate = attendanceStatisticsService.getAttendanceStatisticsByMember(startDate, endDate);
        return ApiResponse.success(statisticsDate, 200, "SUCCESS");
    }

    @GetMapping("/member/details")
    @Operation(summary = "클래스 구성원별 통계 세부 조회", description = "구성원별 출결 통계에서 세부 정보 조회 시 사용되는 api 입니다.")
    public ApiResponse<List<String>> getAttendanceDetailsByMemberAndStatus(
        @Parameter(description = "조회 학생")
        @RequestParam(value = "studentId") Long studentId,
        @Parameter(description = "조회하려는 출결 상태")
        @RequestParam(value = "status") AttendanceStatus status
    ) {
        List<String> studentDetails = attendanceStatisticsService.getAttendanceDetailsByMemberAndStatus(studentId, status);
        return ApiResponse.success(studentDetails, 200, "SUCCESS");
    }
}
