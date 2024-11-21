package classfit.example.classfit.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class EnumValidator implements ConstraintValidator<EnumValue, String> {

    private EnumValue enumValue;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumValue = constraintAnnotation;
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        final Enum<?>[] enumConstants = this.enumValue.target().getEnumConstants();

        if (enumConstants == null) {
            return false;
        }

        return Arrays.stream(enumConstants)
            .anyMatch(enumConstant -> convertible(value, enumConstant) || convertibleIgnoreCase(value, enumConstant));
    }

    private boolean convertible(final String value, final Enum<?> enumConstant) {
        return value.trim().equals(enumConstant.name());
    }

    private boolean convertibleIgnoreCase(final String value, final Enum<?> enumConstant) {
        return this.enumValue.ignoreCase() && value.trim().equalsIgnoreCase(enumConstant.name());
    }
}
