package classfit.example.classfit.mail.controller.docs;

import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.mail.dto.request.EmailRequest;
import classfit.example.classfit.mail.dto.request.EmailVerifyRequest;
import classfit.example.classfit.mail.dto.response.EmailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "이메일 컨트롤러", description = "이메일 관련 API")
public interface EmailControllerDocs {

    @Operation(summary = "이메일 전송", description = "특정 이메일에 인증코드를 전송하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "이메일 전송 성공")
    })
    CustomApiResponse<EmailResponse> sendMail(@RequestBody EmailRequest request);

    @Operation(summary = "이메일 인증코드 검증", description = "인증코드를 검증하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "이메일 인증코드 검증 성공")
    })
    CustomApiResponse<EmailResponse> verifyMail(@RequestBody EmailVerifyRequest request);
}
