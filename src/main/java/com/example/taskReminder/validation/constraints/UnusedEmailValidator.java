package com.example.taskReminder.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.taskReminder.service.UserService;

@Component
public class UnusedEmailValidator implements ConstraintValidator<UnusedEmail, String> {

    @Autowired
    UserService userService;

    @Override
    public void initialize(UnusedEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return userService.isUnusedEmail(value);
    }

}