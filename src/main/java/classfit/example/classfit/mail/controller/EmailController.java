package classfit.example.classfit.mail.controller;

import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.mail.dto.request.EmailRequest;
import classfit.example.classfit.mail.dto.request.EmailVerifyRequest;
import classfit.example.classfit.mail.dto.response.EmailResponse;
import classfit.example.classfit.mail.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
@Tag(name = "이메일 컨트롤러", description = "이메일 관련 API")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    @Operation(summary = "이메일 전송", description = "특정 이메일에 인증코드를 전송하는 API 입니다.")
    public ApiResponse<EmailResponse> sendMail(@RequestBody @Valid EmailRequest request) {
        EmailResponse emailAuthResponse = emailService.sendEmail(request.email(), request.purpose());
        return ApiResponse.success(emailAuthResponse, 200, "이메일이 전송되었습니다");
    }

    @PostMapping("/verify")
    @Operation(summary = "이메일 인증코드 검증", description = "인증코드를 검증하는 API 입니다.")
    public ApiResponse<EmailResponse> verifyMail(@RequestBody @Valid EmailVerifyRequest request) {
        EmailResponse emailAuthResponse = emailService.verifyAuthCode(request);
        return ApiResponse.success(emailAuthResponse, 200, "정상적으로 확인되었습니다.");
    }
}
