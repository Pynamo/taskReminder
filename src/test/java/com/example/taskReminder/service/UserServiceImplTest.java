package com.example.taskReminder.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.taskReminder.entity.User;
import com.example.taskReminder.exception.ResourceNotFoundException;
import com.example.taskReminder.repository.UserRepository;

public class UserServiceImplTest {
	
	@Mock
	UserRepository userRepository;
	
	@InjectMocks
	UserServiceImpl userServiceImpl;
	
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
	class isUnusedEmailの検証 {
		
		@Test
		void emailが既にDBに存在している時にfalseを返す() {
			
			String email = "dummy@dummy.com";
			User user = new User();
			
			when(userRepository.findByUsername(email)).thenReturn(user);
			assertThat(userServiceImpl.isUnusedEmail(email)).isEqualTo(false);

		}
		
		@Test
		void emailがDBに存在していない時にtrueを返す() {
			
			String email = "dummy@dummy.com";
			
			when(userRepository.findByUsername(email)).thenReturn(null);
			assertThat(userServiceImpl.isUnusedEmail(email)).isEqualTo(true);	
			
		}
	}
	
	@Nested
	class findOneの検証 {
		
		@Test
		void userがnullの時にResourceNotFoundException送出() {
			
			String email = "dummy@dummy.com";
			User user = null;
			
			when(userRepository.findByUsername(email)).thenReturn(user);
			
			assertThrows(ResourceNotFoundException.class, () -> userServiceImpl.findOne(email));
				
		}
		
		@Test
		void userの取得成功() throws Exception {
			
			String email = "dummy@dummy.com";
			User user = new User();
			
			when(userRepository.findByUsername(email)).thenReturn(user);
			
			assertThat(userServiceImpl.findOne(email)).isEqualTo(user);
			
		}
		
	}

}