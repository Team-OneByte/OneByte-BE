package classfit.example.classfit.member.controller;

import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.member.dto.request.MemberPasswordRequest;
import classfit.example.classfit.member.dto.request.MemberRequest;
import classfit.example.classfit.member.dto.response.MemberResponse;
import classfit.example.classfit.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/")
@Tag(name = "회원 컨트롤러", description = "회원 관련 API입니다.")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign_in")
    @Operation(summary = "회원 가입", description = "회원가입 API 입니다.")
    public ApiResponse<MemberResponse> signIn(@RequestBody @Valid MemberRequest request) {
        MemberResponse memberResponse = memberService.signIn(request);
        return ApiResponse.success(memberResponse, 200, "회원가입이 완료되었습니다.");
    }

    @PostMapping("/password")
    @Operation(summary = "비밀번호 수정", description = "특정 회원의 비밀번호 수정 API 입니다.")
    public ApiResponse<String> updatePassword(@RequestBody @Valid MemberPasswordRequest request) {
        memberService.updatePassword(request);
        return ApiResponse.success(null, 200, "비밀번호가 변경되었습니다.");
    }
}
