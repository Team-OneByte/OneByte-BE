package classfit.example.classfit.auth.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.auth.service.AuthService;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "회원 컨트롤러", description = "회원 관련 API입니다.")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "토큰 재발급하는 API 입니다.")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<?> responseEntity = authService.reissue(request, response);
        return responseEntity;
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃 API 입니다.")
    public CustomApiResponse<String> logout(@AuthMember Member member) {
        authService.logout(member);
        return CustomApiResponse.success(null, 200, "로그아웃이 완료되었습니다.");
    }
}
