package com.example.taskReminder.service;


import java.util.Collection;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.taskReminder.common.Authority;
import com.example.taskReminder.entity.User;

@Configuration
public class TestUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return new User(username, "name", "password", Authority.ROLE_USER);
	}
	/*
    private static class TestUser extends User {
        private TestUser(Long userId, String username, String name, String password, Authority auth) {
            super();
        }
    }
    */

}
