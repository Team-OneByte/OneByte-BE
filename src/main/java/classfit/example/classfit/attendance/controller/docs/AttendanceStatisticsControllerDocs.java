package classfit.example.classfit.attendance.controller.docs;

import classfit.example.classfit.attendance.domain.AttendanceStatus;
import classfit.example.classfit.attendance.dto.response.StatisticsDateResponse;
import classfit.example.classfit.attendance.dto.response.StatisticsMemberResponse;
import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "출결 통계 컨트롤러", description = "출결 통계 관련 API입니다.")
public interface AttendanceStatisticsControllerDocs {

    @Operation(summary = "클래스 날짜별 통계", description = "날짜별 출결 통계 조회 시 사용되는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "날짜별 출결 통계 조회 성공")
    })
    CustomApiResponse<List<StatisticsDateResponse>> getAttendanceStatisticsByDate(
        @AuthMember Member member,
        @Parameter(description = "조회 시작 날짜") @RequestParam("startDate") LocalDate startDate,
        @Parameter(description = "조회 종료 날짜") @RequestParam("endDate") LocalDate endDate,
        @Parameter(description = "서브 클래스 ID") @RequestParam("subClassId") Long subClassId
    );

    @Operation(summary = "클래스 날짜별 통계 세부 조회", description = "날짜별 출결 통계에서 세부 정보 조회 시 사용되는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "출결 세부 정보 조회 성공")
    })
    CustomApiResponse<List<String>> getAttendanceDetailsByDateAndStatus(
        @AuthMember Member member,
        @Parameter(description = "조회 날짜") @RequestParam("date") LocalDate date,
        @Parameter(description = "서브 클래스 ID") @RequestParam("subClassId") Long subClassId,
        @Parameter(description = "출결 상태") @RequestParam("status") AttendanceStatus status
    );

    @Operation(summary = "구성원별 출결 통계 조회", description = "구성원별로 출결 통계를 조회하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "구성원별 출결 통계 조회 성공")
    })
    CustomApiResponse<List<StatisticsMemberResponse>> getAttendanceStatisticsByMember(
        @AuthMember Member member,
        @Parameter(description = "조회 시작 날짜") @RequestParam("startDate") LocalDate startDate,
        @Parameter(description = "조회 종료 날짜") @RequestParam("endDate") LocalDate endDate
    );

    @Operation(summary = "구성원별 통계 세부 조회", description = "구성원별 출결 통계에서 세부 정보 조회 시 사용되는 API 입니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "구성원별 통계 세부 조회 성공")
        })
    CustomApiResponse<List<String>> getAttendanceDetailsByMemberAndStatus(
        @AuthMember Member member,
        @Parameter(description = "조회 학생") @RequestParam("studentId") Long studentId,
        @Parameter(description = "조회 월") @RequestParam("month") int month,
        @Parameter(description = "출결 상태") @RequestParam("status") AttendanceStatus status
    );
}

