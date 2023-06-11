package com.example.taskReminder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.taskReminder.entity.User;
import com.example.taskReminder.exception.ResourceNotFoundException;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (username == null || "".equals(username)) {
            throw new UsernameNotFoundException("Username is empty");
        }
        
        try {
        	User user = userService.findOne(username);
        	return user;
        } catch(ResourceNotFoundException e) {
        	throw new UsernameNotFoundException("user not found", e);
        }
    }

}