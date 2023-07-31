package com.example.taskReminder.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.example.taskReminder.entity.User;
import com.example.taskReminder.exception.ResourceNotFoundException;


public class UserDetailsServiceImplTest {

	
	@Mock
	UserService userService;
	
	@InjectMocks
	UserDetailsServiceImpl userDetailsService;
	
	private AutoCloseable closeable;
	
	@BeforeEach
	void openMocks() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void releaseMocks() throws Exception {
		closeable.close();
	}
	
	
	@Nested
	class loadUserByUsernameの検証 {
		
		@Test
		void usernameがnullの時にUsernameNotFoundException送出() {
			
			String username = null;
			assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
		}
		
		@Test
		void usernameが空文字の時にUsernameNotFoundException送出() {
			
			String username = "";
			assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));	
		}
		
		@Test
		void usernameがDBに未登録の時にUsernameNotFoundException送出() throws Exception {
			
			String username = "username";
			Mockito.doThrow(ResourceNotFoundException.class).when(userService).findOne(username);
			assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));	
			
		}
		
		@Test
		void userのロード成功() throws Exception {
			
			String username = "username";
			User user = new User();
			
			when(userService.findOne(username)).thenReturn(user);
			
			try {
				userDetailsService.loadUserByUsername(username);
			} catch(UsernameNotFoundException e) {
				fail();
			}
			
		}
		
	}
}
