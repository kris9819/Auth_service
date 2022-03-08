package com.auth.AuthService.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidatorEmail.class)
public @interface ValidationEmail {
    String message() default "Niepoprawny adres e-mail";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
