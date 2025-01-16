package classfit.example.classfit.attendance.controller;

import classfit.example.classfit.attendance.dto.response.StudentAttendanceResponse;
import classfit.example.classfit.attendance.service.AttendanceExportService;
import classfit.example.classfit.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Tag(name = "출결 엑셀 다운로드 컨트롤러", description = "엑셀 다운로드 관련 API입니다.")
public class AttendanceExportController {
    private final AttendanceExportService attendanceExportService;

    @GetMapping("/excel/download")
    @Operation(summary = "엑셀 다운로드", description = "월별 출결 데이터 엑셀 다운로드 api 입니다.")
    public ApiResponse<List<StudentAttendanceResponse>> exportAttendance(
            @Parameter(description = "다운로드 받을 달 (1~12 사이의 값)")
            @RequestParam("month") int month,
            @Parameter(description = "서브 클래스 아이디 (필수 아님)")
            @RequestParam(required = false) Long subClassId) {
        List<StudentAttendanceResponse> attendanceData = attendanceExportService.getAttendanceData(month, subClassId);
        return ApiResponse.success(attendanceData, 200, "SUCCESS");
    }
}
