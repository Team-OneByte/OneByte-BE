package classfit.example.classfit.member.controller.docs;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.dto.request.MemberPasswordRequest;
import classfit.example.classfit.member.dto.request.MemberRequest;
import classfit.example.classfit.member.dto.request.MemberUpdateInfoRequest;
import classfit.example.classfit.member.dto.response.MemberInfoResponse;
import classfit.example.classfit.member.dto.response.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "회원 컨트롤러", description = "회원 관련 API입니다.")
public interface MemberControllerDocs {

    @Operation(summary = "회원 가입", description = "회원가입 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "회원가입이 완료되었습니다.")
    })
    CustomApiResponse<MemberResponse> signUp(@RequestBody @Valid MemberRequest request);

    @Operation(summary = "비밀번호 수정", description = "직접 회원의 비밀번호 수정 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "비밀번호가 변경되었습니다.")
    })
    CustomApiResponse<Void> updatePassword(@RequestBody @Valid MemberPasswordRequest request);

    @Operation(summary = "회원정보 조회", description = "아이디에 기반한 회원정보 조회 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "SUCCESS")
    })
    CustomApiResponse<MemberInfoResponse> myPage(@AuthMember Member member);

    @Operation(summary = "회원정보 수정", description = "회원 정보를 수정하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "SUCCESS")
    })
    CustomApiResponse<MemberInfoResponse> updateMyPage(
        @AuthMember Member member,
        @RequestBody @Valid MemberUpdateInfoRequest request
    );
}
