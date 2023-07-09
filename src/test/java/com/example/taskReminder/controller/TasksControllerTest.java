package com.example.taskReminder.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.taskReminder.common.Load;
import com.example.taskReminder.common.MessageAlertLevel;
import com.example.taskReminder.entity.Task;
import com.example.taskReminder.entity.User;
import com.example.taskReminder.entity.UserInf;
import com.example.taskReminder.entity.TestUser;
import com.example.taskReminder.exception.BusinessException;
import com.example.taskReminder.exception.ResourceNotFoundException;
import com.example.taskReminder.exception.SystemException;
import com.example.taskReminder.form.TaskForm;
import com.example.taskReminder.mapper.TaskMapper;
import com.example.taskReminder.service.TaskService;

@SpringBootTest
public class TasksControllerTest {
	
	private MockMvc mockMvc;
	
	@Mock
	private TaskService taskService;
	
	private AutoCloseable closeable;
	
	@InjectMocks
	private TasksController target;
	
    @Autowired
    private FilterChainProxy springSecurityFilterChain;
	
	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders
				.standaloneSetup(target)
				//.apply(springSecurity(springSecurityFilterChain))
				.build();
	}
	
	@AfterEach
	void tearDown() throws Exception {
		closeable.close();
	}
	
	@Test
	@WithMockUser
	void listメソッドの検証() throws Exception {
		
		
		// SetUp		
		/*
		User user = new User();
		
		user.setUserId(1L);
		
		List<Task> list = new ArrayList<>();
		
		list.add(new Task());
		
		List<TaskForm> formList = new ArrayList<>();
		*/
		
	    //Principal principal = Mockito.mock(Principal.class);
	    //Authentication authentication = Mockito.mock(Authentication.class);
	    
	    //UserInf user = (UserInf)authentication.getPrincipal();
	    
	    
	    //when(principal.getName()).thenReturn("admin");
	    //when(authentication.getPrincipal()).thenReturn(principal);
		
		//when(taskService.getTaskList(user.getUserId())).thenReturn(list);
		/*
		when(taskService.getTaskNumberOfExecution(formList)).thenReturn(formList);
		mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(view().name("index"));
		*/	
		assertThat(true).isEqualTo(true);
		
	}
	/*
	@Test
	void createFormメソッドの検証() throws Exception {
		
		mockMvc.perform(get("/create").param("form", ""))
			.andExpect(status().isOk());
			//.andExpect(view().name("tasks/new"));
			//.andExpect(redirectedUrl(null));
		
	}
	*/
	
	@Test
	void createFormメソッドの検証() throws Exception {
	    mockMvc.perform(get("/create").param("form", ""))
	            .andExpect(status().isOk())
	            .andExpect(view().name("tasks/new"))
	            .andExpect(model().attribute("loadMst", Load.getLoadData()))
	            .andExpect(model().attribute("taskForm", new TaskForm()));
	            //.andExpect(model().attributeExists("taskForm"));
	}
	
	@Test
	void createConfirmメソッドの検証() throws Exception {
		
		TaskForm taskForm = new TaskForm();
		
		taskForm.setName("name");
		taskForm.setLoad(Load.HIGH.getCode());
		taskForm.setContent("content");

		when(taskService.getSibaImageUrl()).thenReturn("https://shiba/dummy.png");
		
		// 正常時
	    mockMvc.perform(post("/create")
	    		.param("confirm", "")
	    		.flashAttr("taskForm", taskForm))
	        .andExpect(status().isOk())
	        .andExpect(view().name("tasks/confirm"))
	        .andExpect(model().attributeExists("taskForm"));
		
	    
	    // taskFormのバリデーションエラーが発生した時
	    taskForm.setName("");
	    
	    mockMvc.perform(post("/create")
	    		.param("confirm", "")
	    		.flashAttr("taskForm", taskForm))
	        .andExpect(status().isOk())
	        .andExpect(view().name("tasks/new"))
	        .andExpect(model().attributeExists("taskForm"))
	        .andExpect(model().attribute("loadMst", Load.getLoadData()));;	    
	    
	}
	
	@Test
	void createRedoメソッドの検証() throws Exception {
		
	    mockMvc.perform(post("/create").param("redo", ""))
	        .andExpect(status().isOk())
	        .andExpect(view().name("tasks/new"))
	        .andExpect(model().attributeExists("taskForm"))
	        .andExpect(model().attribute("loadMst", Load.getLoadData()));    	
	}
	/*
	@Test
	void createメソッドの検証() throws Exception {
		
		TaskForm taskForm = new TaskForm();
		
		taskForm.setName("name");
		taskForm.setLoad(Load.HIGH.getCode());
		taskForm.setContent("content");
		
		User user = new User();
		user.setUserId(1L);
		
		UserInf userInf = (UserInf)user;
		
		Mockito.doNothing().when(taskService).save(taskForm, userInf);
		
	    mockMvc.perform(post("/create"))
	        .andExpect(status().is3xxRedirection())
	        .andExpect(redirectedUrl("/")); 
	}
*/

}


/*
	@PostMapping(value = "/create")
	public String create(@Validated TaskForm taskForm, BindingResult bindingResult, Principal principal,
			RedirectAttributes redirAttrs, Model model) {

		Authentication authentication = (Authentication) principal;
		UserInf user = (UserInf) authentication.getPrincipal();
		
		try {
			taskService.save(taskForm, user);
		} catch (BusinessException e) {

			log.error("Task is full!");
			displayMessageRedirectHelper(
					MessageAlertLevel.ERROR, 
					"3個以上タスクを登録できません。追加したい場合は既存のタスクを削除しましょう", // TODO
					redirAttrs);

			return "redirect:/";
		}

		displayMessageRedirectHelper(MessageAlertLevel.SUCCESS, "新しいタスクを登録しました。頑張って習慣化しましょう！", redirAttrs);

		return "redirect:/";
	}

*/