package classfit.example.classfit.member.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.member.controller.docs.MemberControllerDocs;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.dto.request.MemberPasswordRequest;
import classfit.example.classfit.member.dto.request.MemberRequest;
import classfit.example.classfit.member.dto.request.MemberUpdateInfoRequest;
import classfit.example.classfit.member.dto.response.MemberInfoResponse;
import classfit.example.classfit.member.dto.response.MemberResponse;
import classfit.example.classfit.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/")
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;

    @Override
    @PostMapping("/signup")
    public CustomApiResponse<MemberResponse> signUp(@RequestBody @Valid MemberRequest request) {
        MemberResponse memberResponse = memberService.signUp(request);
        return CustomApiResponse.success(memberResponse, 200, "회원가입이 완료되었습니다.");
    }

    @Override
    @PostMapping("/password")
    public CustomApiResponse<Void> updatePassword(@RequestBody @Valid MemberPasswordRequest request) {
        memberService.updatePassword(request);
        return CustomApiResponse.success(null, 200, "비밀번호가 변경되었습니다.");
    }

    @Override
    @GetMapping("/my-page")
    public CustomApiResponse<MemberInfoResponse> myPage(@AuthMember Member member) {
        MemberInfoResponse memberInfoResponse = memberService.myPage(member);
        return CustomApiResponse.success(memberInfoResponse, 200, "SUCCESS");
    }

    @Override
    @PostMapping("/my-page")
    public CustomApiResponse<MemberInfoResponse> updateMyPage(
        @AuthMember Member member,
        @RequestBody MemberUpdateInfoRequest request
    ) {
        MemberInfoResponse memberInfoResponse = memberService.updateMyPage(member, request);
        return CustomApiResponse.success(memberInfoResponse, 200, "SUCCESS");
    }
}
