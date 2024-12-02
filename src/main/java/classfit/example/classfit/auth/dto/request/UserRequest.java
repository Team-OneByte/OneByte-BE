package classfit.example.classfit.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest
    (
        @NotBlank(message = "이메일은 공백일 수 없습니다.")
        @Email(message = "형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8 ~ 20자리로 입력해 주세요")
        String password
    ) {
}
