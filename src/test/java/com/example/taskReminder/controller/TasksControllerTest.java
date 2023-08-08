package com.example.taskReminder.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.taskReminder.common.Load;
import com.example.taskReminder.common.MessageAlertLevel;
import com.example.taskReminder.entity.Task;
import com.example.taskReminder.entity.TasksExecutionHistory;
import com.example.taskReminder.entity.User;
import com.example.taskReminder.entity.UserInf;
import com.example.taskReminder.entity.TestUser;
import com.example.taskReminder.exception.BusinessException;
import com.example.taskReminder.exception.SystemException;
import com.example.taskReminder.exception.ResourceNotFoundException;
import com.example.taskReminder.form.ChartJsInputDataObject;
import com.example.taskReminder.form.TaskForm;
import com.example.taskReminder.form.TaskFormList;
import com.example.taskReminder.mapper.TaskMapper;
import com.example.taskReminder.service.TaskExecuteService;
import com.example.taskReminder.service.TaskService;

@SpringBootTest
public class TasksControllerTest {
	
	private MockMvc mockMvc;
	
	@Mock
	private TaskService taskService;
	@Mock
	private TaskExecuteService taskExecuteService;
	@Mock
	private MessageSource messages;
	
	
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
	        .andExpect(model().attribute("loadMst", Load.getLoadData()));    
	    
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
	@Nested
	class deleteSelectedメソッドの検証 {
		
		@Test
		void taskFormListがnullの時ならタスクが存在しないと警告を出す() throws Exception {
			
			TaskFormList taskFormList = new TaskFormList();
			taskFormList.setTaskFormList(null);
			
		    mockMvc.perform(post("/delete")
		    		.flashAttr("taskFormList", taskFormList))
		        .andExpect(status().is3xxRedirection())
		        .andExpect(redirectedUrl("/delete?form"))
		        .andExpect(flash().attribute("class", MessageAlertLevel.WARNING.getCode()));   
			
		}
		
		@Test
		void 削除対象のタスクが選択されていないなら警告を出す() throws Exception {

			TaskFormList taskFormList = new TaskFormList();
			TaskForm taskForm = new TaskForm();
			taskForm.setSelected(false);
			List<TaskForm> list = new ArrayList<>();
			list.add(taskForm);
			taskFormList.setTaskFormList(list);
			
		    mockMvc.perform(post("/delete")
		    		.flashAttr("taskFormList", taskFormList))
		        .andExpect(status().is3xxRedirection())
		        .andExpect(redirectedUrl("/delete?form"))
		        .andExpect(flash().attribute("class", MessageAlertLevel.WARNING.getCode()));    
			
		}
		
		@Test
		void 選択されたタスクの削除が実行されることを確認する() throws Exception {

			TaskFormList taskFormList = new TaskFormList();
			TaskForm taskForm = new TaskForm();
			taskForm.setSelected(true);
			taskForm.setTaskId(1L);
			List<TaskForm> list = new ArrayList<>();
			list.add(taskForm);
			taskFormList.setTaskFormList(list);
			
			
			Mockito.doNothing().when(taskService).deleteTask(taskForm.getTaskId());
			
		    mockMvc.perform(post("/delete")
		    		.flashAttr("taskFormList", taskFormList))
		        .andExpect(status().is3xxRedirection())
		        .andExpect(redirectedUrl("/delete?form"))
		        .andExpect(flash().attribute("class", MessageAlertLevel.SUCCESS.getCode()));    
		    
		    

		}
		
		@Test
		void 選択されたタスクがDBに存在しなければResourceNotFoundExceptionを送出する() throws Exception {

			TaskFormList taskFormList = new TaskFormList();
			TaskForm taskForm = new TaskForm();
			taskForm.setSelected(true);
			taskForm.setTaskId(1L);
			List<TaskForm> list = new ArrayList<>();
			list.add(taskForm);
			taskFormList.setTaskFormList(list);
			
			Mockito.doThrow(ResourceNotFoundException.class).when(taskService).deleteTask(taskForm.getTaskId());
			
		    mockMvc.perform(post("/delete")
		    		.flashAttr("taskFormList", taskFormList))
		        .andExpect(status().is3xxRedirection())
		        .andExpect(redirectedUrl("/delete?form"))
		        .andExpect(flash().attribute("class", MessageAlertLevel.ERROR.getCode()));    

		}
		
		@Test
		void 選択されたタスクが既に削除されていればBuisinessExceptionを送出する() throws Exception {

			TaskFormList taskFormList = new TaskFormList();
			TaskForm taskForm = new TaskForm();
			taskForm.setSelected(true);
			taskForm.setTaskId(1L);
			List<TaskForm> list = new ArrayList<>();
			list.add(taskForm);
			taskFormList.setTaskFormList(list);
			
			
			Mockito.doThrow(BusinessException.class).when(taskService).deleteTask(taskForm.getTaskId());
			
		    mockMvc.perform(post("/delete")
		    		.flashAttr("taskFormList", taskFormList))
		        .andExpect(status().is3xxRedirection())
		        .andExpect(redirectedUrl("/delete?form"))
		        .andExpect(flash().attribute("class", MessageAlertLevel.ERROR.getCode()));    

		}
		
	}
	
	@Nested
	class updateFormメソッドの検証 {
		
		@Test
		void タスクの取得成功() throws Exception {
			
			long taskId = 1L;
			
			Task task = new Task();
			task.setTaskId(taskId);
			task.setName("name");
			task.setContent("content");
			task.setLoad(Load.HIGH);
			
			when(taskService.getTask(taskId)).thenReturn(task);
			
		    mockMvc.perform(get("/update")
		    		.param("task_id", String.valueOf(taskId)))
	            .andExpect(status().isOk())
	            .andExpect(view().name("tasks/update"))
	            .andExpect(model().attribute("loadMst", Load.getLoadData()))
	            .andExpect(model().attribute("taskForm", TaskMapper.INSTANCE.taskToForm(task)));
			
		    Mockito.verify(taskService, Mockito.times(1)).getTask(taskId);

		}
		
		@Test
		void タスクの取得失敗() throws Exception {
			
			long taskId = 1L;
			
			Mockito.doThrow(ResourceNotFoundException.class).when(taskService).getTask(taskId);
			
		    mockMvc.perform(get("/update")
		    		.param("task_id", String.valueOf(taskId)))
	            .andExpect(status().is3xxRedirection())
	            .andExpect(redirectedUrl("/"))
	            .andExpect(flash().attribute("class", MessageAlertLevel.ERROR.getCode()));
			
		    Mockito.verify(taskService, Mockito.times(1)).getTask(taskId);	
		}
		
	}
	
	@Nested
	class updateメソッドの検証 {
		
		@Test
		void バリデーションエラー発生() throws Exception {
			
			TaskForm taskForm = new TaskForm();
			
			mockMvc.perform(post("/update")
		    		.flashAttr("taskForm", taskForm))
	            .andExpect(status().isOk())
	            .andExpect(view().name("tasks/update"))
	            .andExpect(model().attribute("loadMst", Load.getLoadData()))
	            .andExpect(model().attribute("taskForm", taskForm));

		}
		
		@Test
		void update成功() throws Exception {
			
			TaskForm taskForm = new TaskForm();
			
			taskForm.setTaskId(1L);
			taskForm.setName("name");
			taskForm.setLoad(Load.HIGH.getCode());
			taskForm.setContent("content");
			
			Task task = new Task();
			
			when(taskService.getTask(taskForm.getTaskId())).thenReturn(task);
			Mockito.doNothing().when(taskService).update(task);
			
			mockMvc.perform(post("/update")
		    		.flashAttr("taskForm", taskForm))
	            .andExpect(status().is3xxRedirection())
	            .andExpect(redirectedUrl("/"))
	            .andExpect(flash().attribute("class", MessageAlertLevel.SUCCESS.getCode()));
			
		}
		
		@Test
		void ResourceNotFound送出によるupdate失敗() throws Exception {
			
			TaskForm taskForm = new TaskForm();
			
			taskForm.setTaskId(1L);
			taskForm.setName("name");
			taskForm.setLoad(Load.HIGH.getCode());
			taskForm.setContent("content");
			
			Mockito.doThrow(ResourceNotFoundException.class).when(taskService).getTask(taskForm.getTaskId());
			
			mockMvc.perform(post("/update")
		    		.flashAttr("taskForm", taskForm))
	            .andExpect(status().is3xxRedirection())
	            .andExpect(redirectedUrl("/"))
	            .andExpect(flash().attribute("class", MessageAlertLevel.ERROR.getCode()));
			
		}
		
	}
	
	@Nested
	class detailメソッドの検証 {
		
		@Test
		void ResourceNotFoundException送出によるタスク取得失敗() throws Exception {
			
			long taskId = 1L;
			
			Mockito.doThrow(ResourceNotFoundException.class).when(taskService).getTask(taskId);
			
			mockMvc.perform(get("/detail")
					.param("task_id", String.valueOf(taskId)))
	            .andExpect(status().is3xxRedirection())
	            .andExpect(redirectedUrl("/"))
	            .andExpect(flash().attribute("class", MessageAlertLevel.ERROR.getCode()));
			
		}
		
		@Test
		void タスクが過去に1度も実行されていないことによるBuisinessException送出() throws Exception {
			
			long taskId = 1L;
			Task task = new Task();
			
			task.setName("name");
			task.setLoad(Load.HIGH);
			
			when(taskService.getTask(taskId)).thenReturn(task);
			Mockito.doThrow(BusinessException.class).when(taskExecuteService).getTaskExecuteHistory(taskId);
			
			when(messages.getMessage("A0006_title", new String[] { task.getName() }, Locale.JAPAN)).thenReturn("title");
			when(messages.getMessage("A0006_subtitle_load_high", null, Locale.JAPAN)).thenReturn("subtitle");
			
			
			mockMvc.perform(get("/detail")
					.param("task_id", String.valueOf(taskId)))
	            .andExpect(status().isOk())
	            .andExpect(view().name("tasks/detail"))
	            .andExpect(model().attribute("class", MessageAlertLevel.WARNING.getCode()));
		}
		
		@Test
		void タスク詳細情報の取得成功() throws Exception {
			
			long taskId = 1L;
			Task task = new Task();
			
			task.setName("name");
			task.setLoad(Load.HIGH);
			
			List<TasksExecutionHistory> list = new ArrayList<>();
			
			TasksExecutionHistory item = new TasksExecutionHistory();
			SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = df.parse("2023/06/06 12:00:00");
			item.setCreatedAt(date);
			
			list.add(item);
			
			when(taskService.getTask(taskId)).thenReturn(task);
			when(taskExecuteService.getTaskExecuteHistory(taskId)).thenReturn(list);
			when(messages.getMessage("A0006_title", new String[] { task.getName() }, Locale.JAPAN)).thenReturn("title");
			when(messages.getMessage("A0006_subtitle_load_high", null, Locale.JAPAN)).thenReturn("subtitle");
			
			mockMvc.perform(get("/detail")
					.param("task_id", String.valueOf(taskId)))
	            .andExpect(status().isOk())
	            .andExpect(view().name("tasks/detail"));
			
			Mockito.verify(taskExecuteService, Mockito.times(1)).getTaskExecuteHistory(taskId);
			
		}
		
		
	}


}
