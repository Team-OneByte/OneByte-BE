package classfit.example.classfit.invitation.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.invitation.controller.docs.InvitationControllerDocs;
import classfit.example.classfit.invitation.dto.request.InvitationRequest;
import classfit.example.classfit.invitation.dto.response.InvitationResponse;
import classfit.example.classfit.invitation.service.InvitationService;
import classfit.example.classfit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/invitation")
public class InvitationController implements InvitationControllerDocs {

    private final InvitationService invitationService;

    @Override
    @GetMapping("/invite")
    public CustomApiResponse<String> findAcademyCode(@AuthMember Member member) {
        String academyCode = invitationService.findAcademyCode(member);
        return CustomApiResponse.success(academyCode, 200, "학원 코드 조회 성공");
    }

    @Override
    @PostMapping("/invite")
    public CustomApiResponse<Void> inviteStaffByEmail(
        @AuthMember Member member,
        @RequestBody InvitationRequest request
    ) {
        invitationService.inviteStaffByEmail(member, request);
        return CustomApiResponse.success(null, 200, "초대 코드 전송 성공");
    }

    @Override
    @GetMapping("/list")
    public CustomApiResponse<List<InvitationResponse>> staffInfoAll(@AuthMember Member member) {
        List<InvitationResponse> staffInfoList = invitationService.staffInfoAll(member);
        return CustomApiResponse.success(staffInfoList, 200, "초대 직원 조회 성공");
    }
}
