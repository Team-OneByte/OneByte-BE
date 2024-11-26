package classfit.example.classfit.mail.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailAuthRequest
    (
        @NotBlank(message = "이메일을 입력해 주세요")
        @Email(message = "올바르지 않은 이메일 형식입니다.")
        String email
    ) {
}
