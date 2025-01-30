package classfit.example.classfit.attendance.controller;

import classfit.example.classfit.attendance.controller.docs.AttendanceExportControllerDocs;
import classfit.example.classfit.attendance.dto.response.StudentAttendanceResponse;
import classfit.example.classfit.attendance.service.AttendanceExportService;
import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class AttendanceExportController implements AttendanceExportControllerDocs {

    private final AttendanceExportService attendanceExportService;

    @GetMapping("/excel/download")
    public CustomApiResponse<List<StudentAttendanceResponse>> exportAttendance(
            @AuthMember Member member,
            @RequestParam("month") int month,
            @RequestParam(required = false) Long subClassId
    ) {
        List<StudentAttendanceResponse> attendanceData = attendanceExportService.getAttendanceData(month, subClassId, member);
        return CustomApiResponse.success(attendanceData, 200, "엑셀 다운로드 성공");
    }
}
