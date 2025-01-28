package classfit.example.classfit.auth.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.auth.controller.docs.AuthControllerDocs;
import classfit.example.classfit.auth.service.AuthService;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
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
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;

    @Override
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<?> responseEntity = authService.reissue(request, response);
        return responseEntity;
    }

    @Override
    @PostMapping("/logout")
    public CustomApiResponse<String> logout(@AuthMember Member member) {
        authService.logout(member);
        return CustomApiResponse.success(null, 200, "로그아웃 성공");
    }
}
