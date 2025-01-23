package classfit.example.classfit.mail.controller;

import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.mail.controller.docs.EmailControllerDocs;
import classfit.example.classfit.mail.dto.request.EmailRequest;
import classfit.example.classfit.mail.dto.request.EmailVerifyRequest;
import classfit.example.classfit.mail.dto.response.EmailResponse;
import classfit.example.classfit.mail.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
public class EmailController implements EmailControllerDocs {

    private final EmailService emailService;

    @PostMapping("/send")
    public CustomApiResponse<EmailResponse> sendMail(@RequestBody @Valid EmailRequest request) {
        EmailResponse emailAuthResponse = emailService.sendEmail(request.email(), request.purpose());
        return CustomApiResponse.success(emailAuthResponse, 200, "이메일 전송 성공");
    }

    @PostMapping("/verify")
    public CustomApiResponse<EmailResponse> verifyMail(@RequestBody @Valid EmailVerifyRequest request) {
        EmailResponse emailAuthResponse = emailService.verifyAuthCode(request);
        return CustomApiResponse.success(emailAuthResponse, 200, "이메일 인증코드 검증 성공");
    }
}
