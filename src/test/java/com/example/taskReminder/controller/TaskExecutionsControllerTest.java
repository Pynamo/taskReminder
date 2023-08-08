package com.example.taskReminder.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.taskReminder.common.MessageAlertLevel;
import com.example.taskReminder.service.TaskExecuteService;
import com.example.taskReminder.exception.BusinessException;

@SpringBootTest
public class TaskExecutionsControllerTest {
	
	private MockMvc mockMvc;
	
	@Mock
	private TaskExecuteService taskExecuteService;
	
	@InjectMocks
	private TaskExecutionsController target;
	
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
	
	@Nested
	class executeメソッドの検証 {
	
		@Test
		void 実行失敗() throws Exception {
			
			long taskId = 1L;
			
			Mockito.doThrow(BusinessException.class).when(taskExecuteService).execute(taskId);
		    
			mockMvc.perform(post("/execute")
					.param("task_id", String.valueOf(taskId)))
	            .andExpect(status().is3xxRedirection())
	            .andExpect(redirectedUrl("/"))
	            .andExpect(flash().attribute("class", MessageAlertLevel.ERROR.getCode()));
		}
		
		
		@Test
		void 実行成功() throws Exception {
			
			long taskId = 1L;
			
			Mockito.doNothing().when(taskExecuteService).execute(taskId);
		    
			mockMvc.perform(post("/execute")
					.param("task_id", String.valueOf(taskId)))
	            .andExpect(status().is3xxRedirection())
	            .andExpect(redirectedUrl("/"))
	            .andExpect(flash().attribute("class", MessageAlertLevel.SUCCESS.getCode()));
			
		}
	
	}
	

}
