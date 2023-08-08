package com.example.taskReminder.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.validation.Validator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.taskReminder.common.Load;
import com.example.taskReminder.common.MessageAlertLevel;
import com.example.taskReminder.exception.BusinessException;
import com.example.taskReminder.form.TaskForm;
import com.example.taskReminder.form.UserForm;
import com.example.taskReminder.service.UserService;
import com.example.taskReminder.service.UserServiceImpl;
import com.example.taskReminder.validation.constraints.UnusedEmailValidator;

@SpringBootTest
public class UsersControllerTest {

	private MockMvc mockMvc;
	
	@Mock
	private UserService userService;
	
	@InjectMocks
    private UnusedEmailValidator unusedEmailValidator;
	
	@InjectMocks
	private UsersController target;
	
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
		
		mockMvc.perform(get("/users/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("users/new"))
            .andExpect(model().attribute("userForm", new UserForm()));
	
	}
	
	@Nested
	class createメソッドの検証 {
		
		@Test
		void バリデーションエラー発生() throws Exception {
			UserForm userForm = new UserForm();
		    
			mockMvc.perform(post("/users/create"))
	            .andExpect(status().isOk())
	            .andExpect(view().name("users/new"))
	            .andExpect(model().attribute("userForm", userForm));
		}
		
	}
	
	
}
