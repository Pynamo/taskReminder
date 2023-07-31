package com.example.taskReminder.validation;

import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.taskReminder.form.UserForm;

@Component
public class PasswordEqualsValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return UserForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		
		UserForm userForm = (UserForm) target;
		String password = userForm.getPassword();
		String passwordConfirmation = userForm.getPasswordConfirmation();
		
		if(!StringUtils.hasLength(password)) {
			return;
		}
		
		if(!Objects.equals(password, passwordConfirmation)) {
			errors.rejectValue(
					"passwordConfirmation",
					"PasswordEqualsValidator.userForm.password",
					"パスワードが異なります");
		}	
	}
}