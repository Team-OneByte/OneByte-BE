package classfit.example.classfit.invitation.controller.docs;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.invitation.dto.request.InvitationRequest;
import classfit.example.classfit.invitation.dto.response.InvitationResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "학원 초대 컨트롤러", description = "학원 초대 관련 API입니다.")
public interface InvitationControllerDocs {

    @Operation(summary = "학원 코드 조회", description = "특정 학원의 코드를 조회하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "학원 코드 조회 성공")
    })
    CustomApiResponse<String> findAcademyCode(@AuthMember Member member);

    @Operation(summary = "직원 이메일 전송", description = "특정 학원에 직원을 초대하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "초대 코드 전송 성공")
    })
    CustomApiResponse<Void> inviteStaffByEmail(
        @AuthMember Member member,
        @RequestBody InvitationRequest request
    );

    @Operation(summary = "초대 직원 조회", description = "특정 학원 초대한 직원을 조회하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "초대 직원 조회 성공")
    })
    CustomApiResponse<List<InvitationResponse>> staffInfoAll(@AuthMember Member member);
}
