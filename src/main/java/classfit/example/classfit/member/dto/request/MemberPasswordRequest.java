package classfit.example.classfit.member.dto.request;

import classfit.example.classfit.common.validation.PasswordMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@PasswordMatch
public record MemberPasswordRequest(
        @NotBlank(message = "이메일은 공백일 수 없습니다.")
        @Email(message = "형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8 ~ 20자리로 입력해 주세요")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-\\[\\]{};':\"\\\\|,.<>\\/?]{8,20}$",
                message = "비밀번호는 영문자 숫자를 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "비밀번호 확인란은 공백일 수 없습니다.")
        String passwordConfirm,

        @NotBlank(message = "이메일 인증번호 확인이 필요합니다.")
        String emailToken
) {
}
