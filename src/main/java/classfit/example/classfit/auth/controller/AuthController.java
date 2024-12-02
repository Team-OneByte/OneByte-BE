package classfit.example.classfit.auth.controller;

import classfit.example.classfit.auth.service.AuthService;
import classfit.example.classfit.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse<HttpServletResponse> reissue(HttpServletRequest request, HttpServletResponse response) {
        HttpServletResponse reissueResponse = authService.reissue(request, response);
        return ApiResponse.success(reissueResponse, 200, "재발급이 완료되었습니다");
    }
}
