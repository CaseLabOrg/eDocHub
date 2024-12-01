package com.example.ecm.exception.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = CustomEnumValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface EnumValidator {
    Class<? extends Enum<?>> enumClass();

    String message() default "Значение должно соответствовать одному из значений перечисления";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}