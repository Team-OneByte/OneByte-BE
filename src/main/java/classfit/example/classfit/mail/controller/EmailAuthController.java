package classfit.example.classfit.mail.controller;

import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.mail.dto.request.EmailAuthRequest;
import classfit.example.classfit.mail.dto.request.EmailAuthVerifyRequest;
import classfit.example.classfit.mail.dto.response.EmailAuthResponse;
import classfit.example.classfit.mail.service.EmailAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mail")
public class EmailAuthController {

    private final EmailAuthService emailAuthService;

    @GetMapping("/send")
    public ApiResponse<EmailAuthResponse> sendMail(@RequestBody @Valid EmailAuthRequest request) {
        EmailAuthResponse emailAuthResponse = emailAuthService.sendEmail(request);
        return ApiResponse.success(emailAuthResponse, 200, "이메일이 전송되었습니다");
    }

    @PostMapping("/verify")
    public ApiResponse<EmailAuthResponse> verifyMail(@RequestBody @Valid EmailAuthVerifyRequest request) {
        EmailAuthResponse emailAuthResponse = emailAuthService.verifyAuthCode(request);
        return ApiResponse.success(emailAuthResponse, 200, "정상적으로 확인되었습니다.");
    }
}
