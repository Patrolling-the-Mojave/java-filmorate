package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = LoginValidator.class)
public @interface Login {
    String message() default "логин не должен содержать пробелов";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
