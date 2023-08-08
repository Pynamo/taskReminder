package com.example.taskReminder.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.taskReminder.common.MessageAlertLevel;

@SpringBootTest
public class SessionsControllerTest {
	
	
	private MockMvc mockMvc;
	
	@InjectMocks
	private SessionsController target;
	
	private AutoCloseable closeable;
	
	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders
				.standaloneSetup(target)
				.build();
	}
	
	@AfterEach
	void tearDown() throws Exception {
		closeable.close();
	}
	
	@Test
	void createFormメソッドの検証() throws Exception {
		
		mockMvc.perform(get("/login"))
        	.andExpect(status().isOk())
        	.andExpect(view().name("sessions/new"));
		
	}
	
	@Test
	void loginFailureメソッドの検証() throws Exception {
		mockMvc.perform(get("/login-failure"))
        	.andExpect(status().isOk())
        	.andExpect(view().name("sessions/new"))
        	.andExpect(model().attribute("class", MessageAlertLevel.ERROR.getCode()));
	}

}
