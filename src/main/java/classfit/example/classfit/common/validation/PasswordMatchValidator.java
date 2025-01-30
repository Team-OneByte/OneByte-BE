package classfit.example.classfit.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        String password = (String) new BeanWrapperImpl(value).getPropertyValue("password");
        String passwordConfirm = (String) new BeanWrapperImpl(value).getPropertyValue("passwordConfirm");

        if (password == null || !password.equals(passwordConfirm)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("비밀번호와 비밀번호 확인이 일치하지 않습니다.")
                    .addConstraintViolation();

            return false;
        }
        return true;
    }
}