package com.auth.AuthService.validation;

import com.auth.AuthService.DTO.UserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MatcherPassword implements ConstraintValidator<MatchPassword, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        UserDTO user = (UserDTO) obj;
        return (user.getPassword().equals(user.getRepeatPassword()));
    }

    @Override
    public void initialize(MatchPassword constraintAnnotation) {
    }
}
