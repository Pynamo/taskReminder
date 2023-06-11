package com.example.taskReminder.service;

import com.example.taskReminder.entity.User;
import com.example.taskReminder.exception.ResourceNotFoundException;
import com.example.taskReminder.form.UserForm;


public interface UserService {

	void save(UserForm userForm);
	boolean isUnusedEmail(String email);
	User findOne(String email) throws ResourceNotFoundException;

}
