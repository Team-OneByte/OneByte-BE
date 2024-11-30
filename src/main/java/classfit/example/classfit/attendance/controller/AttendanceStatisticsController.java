package classfit.example.classfit.attendance.controller;

import classfit.example.classfit.attendance.dto.response.StatisticsDateResponse;
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
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Tag(name = "출결 통계 컨트롤러", description = "출결 통계 관련 API입니다.")
public class AttendanceStatisticsController {
    private final AttendanceStatisticsService attendanceStatisticsService;

    @GetMapping("/statistics/date")
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

    @GetMapping("/statistics/member")
    @Operation(summary = "클래스 구성원별 통계", description = "구성원별 출결 통계 조회 시 사용되는 api 입니다.")
    public ApiResponse getAttendanceStaticsByMember(
        @Parameter(description = "조회 시작 기간")
        @RequestParam(value = "startDate") LocalDate startDate,
        @Parameter(description = "조회 끝 기간")
        @RequestParam(value = "endDate") LocalDate endDate) {


        return ApiResponse.success("", 200, "SUCCESS");
    }
}
