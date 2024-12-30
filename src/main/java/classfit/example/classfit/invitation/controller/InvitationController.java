package classfit.example.classfit.invitation.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.invitation.dto.request.InvitationRequest;
import classfit.example.classfit.invitation.dto.response.InvitationResponse;
import classfit.example.classfit.invitation.service.InvitationService;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/invitation")
@Tag(name = "학원 초대 컨트롤러", description = "학원 초대 관련 API입니다.")
public class InvitationController {

    private final InvitationService invitationService;

    @GetMapping("/invite")
    @Operation(summary = "학원 코드 조회", description = "특정 학원의 코드를 조회하는 API 입니다.")
    public ApiResponse<String> findAcademyCode(@AuthMember Member member) {
        String academyCode = invitationService.findAcademyCode(member);
        return ApiResponse.success(academyCode, 200, "학원 코드 조회가 완료되었습니다");
    }

    @PostMapping("/invite")
    @Operation(summary = "직원 이메일 전송", description = "특정 학원에 직원을 초대하는 API 입니다.")
    public ApiResponse<Nullable> inviteStaffByEmail(@AuthMember Member member, @RequestBody InvitationRequest request) {
        invitationService.inviteStaffByEmail(member, request);
        return ApiResponse.success(null, 200, request.email() + "로 초대 코드 전송했습니다.");
    }

    @GetMapping("/list")
    @Operation(summary = "초대 직원 조회", description = "특정 학원 초대한 직원를 조회하는 API 입니다.")
    public ApiResponse<List<InvitationResponse>> staffInfoAll(@AuthMember Member member) {
        List<InvitationResponse> staffInfoList = invitationService.staffInfoAll(member);
        return ApiResponse.success(staffInfoList, 200, "SUCCESS");
    }
}
