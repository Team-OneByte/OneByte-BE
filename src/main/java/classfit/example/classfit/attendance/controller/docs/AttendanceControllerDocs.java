package classfit.example.classfit.attendance.controller.docs;

import classfit.example.classfit.attendance.dto.request.StudentAttendanceUpdateRequest;
import classfit.example.classfit.attendance.dto.response.StudentAttendanceResponse;
import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Tag(name = "출결 관리 컨트롤러", description = "출결 관리 API입니다.")
public interface AttendanceControllerDocs {

    @Operation(summary = "전체 학생 출결 조회", description = "전체 학생의 주차별 출결 정보를 조회하는 API입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "전체 학생 출결 조회 성공")
    })
    CustomApiResponse<List<StudentAttendanceResponse>> getAttendance(
        @AuthMember Member member,
        @Parameter(description = "이전 또는 이후 주의 출결 정보 조회를 위한 오프셋 값입니다.") @RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset,
        @Parameter(description = "페이징 처리를 위한 페이지 번호입니다.") @RequestParam(value = "page", defaultValue = "0") int page
    );

    @Operation(summary = "특정 클래스 출결 조회", description = "특정 클래스의 주차별 출결 정보를 조회하는 API입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "특정 클래스 출결 조회 성공")
    })
    CustomApiResponse<List<StudentAttendanceResponse>> getClassAttendance(
        @AuthMember Member member,
        @Parameter(description = "이전 또는 이후 주의 출결 정보 조회를 위한 오프셋 값입니다.") @RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset,
        @Parameter(description = "페이징 처리를 위한 페이지 번호입니다.") @RequestParam(value = "page", defaultValue = "0") int page,
        @PathVariable Long mainClassId,
        @PathVariable Long subClassId
    );

    @Operation(summary = "학생 출결 정보 수정", description = "학생 출결 정보를 수정하는 API입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "출결 수정 성공")
    })
    CustomApiResponse<List<StudentAttendanceResponse>> updateAttendance(
        @AuthMember Member member,
        @Valid @RequestBody List<StudentAttendanceUpdateRequest> requestDTO
    );
}
