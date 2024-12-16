package classfit.example.classfit.mail.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "이메일 전송 DTO")
public record EmailAuthRequest
    (
        @NotBlank(message = "이메일을 입력해 주세요")
        @Email(message = "올바르지 않은 이메일 형식입니다.")
        String email,

        @Schema(description = "회원가입 >> SIGN_IN , 비밀번호 찾기 >> PASSWORD_RESET")
        EmailAuthPurpose purpose
    ) {
}
