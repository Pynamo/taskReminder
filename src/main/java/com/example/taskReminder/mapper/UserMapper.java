package com.example.taskReminder.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.taskReminder.common.Authority;
import com.example.taskReminder.entity.User;
import com.example.taskReminder.form.UserForm;

@Mapper
public interface UserMapper {

	
	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
	
	default User userFormToUser(UserForm userForm) {
		
		User user = new User();
		user.setName(userForm.getName());
		user.setUsername(userForm.getEmail());
		user.setAuthority(Authority.ROLE_USER);
		//user.setPassword(passwordEncoder.encode(userForm.getPassword()));
		
		return user;
	}
}