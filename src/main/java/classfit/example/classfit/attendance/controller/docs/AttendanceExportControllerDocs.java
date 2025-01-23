package classfit.example.classfit.attendance.controller.docs;

import classfit.example.classfit.attendance.dto.response.StudentAttendanceResponse;
import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "출결 엑셀 다운로드 컨트롤러", description = "엑셀 다운로드 관련 API입니다.")
public interface AttendanceExportControllerDocs {

    @Operation(summary = "엑셀 다운로드", description = "월별 출결 데이터 엑셀 다운로드 API입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "엑셀 다운로드 성공")
    })
    CustomApiResponse<List<StudentAttendanceResponse>> exportAttendance(
        @AuthMember Member member,
        @Parameter(description = "다운로드 받을 달 (1~12 사이의 값)") @RequestParam("month") int month,
        @Parameter(description = "서브 클래스 아이디 (필수 아님)") @RequestParam(required = false) Long subClassId
    );
}
