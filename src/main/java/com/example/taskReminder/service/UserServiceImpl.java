package com.example.taskReminder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.taskReminder.common.Authority;
import com.example.taskReminder.form.UserForm;
import com.example.taskReminder.repository.UserRepository;
import com.example.taskReminder.entity.User;
import com.example.taskReminder.exception.ResourceNotFoundException;
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public void save(UserForm userForm) {
		
		String name = userForm.getName();
        String email = userForm.getEmail();
        String password = userForm.getPassword();

        User entity = new User(
        		email, 
        		name, 
        		passwordEncoder.encode(password), 
        		Authority.ROLE_USER);
        
        userRepository.saveAndFlush(entity);
	}

	@Override
	public boolean isUnusedEmail(String email) {
		if (userRepository.findByUsername(email) != null) {
            return false;
        }
		return true;
	}

	@Override
	public User findOne(String email) throws ResourceNotFoundException {
		User user = userRepository.findByUsername(email);
		if(user == null) {
			throw new ResourceNotFoundException("The given account is not found! username=" + email);
		}
		return user;
	}
}
