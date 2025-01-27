package classfit.example.classfit.auth.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Optional;
import java.util.Set;

public record UserRequest(
    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String email,

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8 ~ 20자리로 입력해 주세요")
    String password
) {
    public Optional<String> validate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(this);

        return violations.stream()
            .map(ConstraintViolation::getMessage)
            .findFirst();
    }
}
