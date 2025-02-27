package classfit.example.classfit.auth.custom;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

@Getter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private final String email;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8 ~ 20자로 입력해 주세요")
    private final String password;

    public CustomAuthenticationToken(String email, String password, Collection<? extends GrantedAuthority> authorities) {
        super(email, password, authorities);
        this.email = email;
        this.password = password;
        validate();
    }

    private void validate() {
        Set<ConstraintViolation<CustomAuthenticationToken>> violations = validator.validate(this);
        violations.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .ifPresent(errorMessage -> {
                    throw new BadCredentialsException(errorMessage);
                });
    }
}

