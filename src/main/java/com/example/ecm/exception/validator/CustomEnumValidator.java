package com.example.ecm.exception.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class CustomEnumValidator implements ConstraintValidator<EnumValidator, String> {
    private Enum<?>[] enumConstants;

    @Override
    public void initialize(EnumValidator constraintAnnotation) {
        enumConstants = constraintAnnotation.enumClass().getEnumConstants();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null){
            return false;
        }
        return Arrays.stream(enumConstants)
                .anyMatch(e -> e.name().equals(value));
    }
}
