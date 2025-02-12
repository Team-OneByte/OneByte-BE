package classfit.example.classfit.common.annotation.handler;

import classfit.example.classfit.common.annotation.NotBlankNullable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotBlankNullableValidator implements ConstraintValidator<NotBlankNullable, String> {

    @Override
    public void initialize(NotBlankNullable constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return !value.trim().isEmpty();
    }
}
