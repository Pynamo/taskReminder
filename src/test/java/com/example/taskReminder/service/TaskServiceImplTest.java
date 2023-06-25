package com.example.taskReminder.service;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.taskReminder.common.Delete;
import com.example.taskReminder.common.Load;
import com.example.taskReminder.entity.Task;
import com.example.taskReminder.entity.UserInf;
import com.example.taskReminder.entity.User;
import com.example.taskReminder.exception.BusinessException;
import com.example.taskReminder.exception.ResourceNotFoundException;
import com.example.taskReminder.exception.SystemException;
import com.example.taskReminder.form.TaskForm;
import com.example.taskReminder.repository.TaskExecutionHistoryRepository;
import com.example.taskReminder.repository.TaskRepository;


public class TaskServiceImplTest {

	@Mock
	TaskRepository taskRepository;
	@Mock
	TaskExecutionHistoryRepository taskExecutionHistoryRepository;
	
	@InjectMocks
	TaskServiceImpl taskServiceImpl;
	
	private AutoCloseable closeable;
	
	TaskForm taskForm;
	Task task;
	UserInf user;
	List<Task> taskList;

	// 各テストの前に実行
	@BeforeEach
	void openMocks() {
		closeable = MockitoAnnotations.openMocks(this);
	}
	
	@BeforeEach
	void setUp() {
		
		taskForm = new TaskForm();
		task = new Task();
		user = new User();
		taskList = new ArrayList<>(); 
		
		task.setTaskId(1L);
		task.setUserId(1L);
		taskList.add(task);
	}

	// 各テストの後に実行
	@AfterEach
	void releaseMocks() throws Exception {
		closeable.close();
	}
	
	
	@Nested
	class saveメソッドの検証 {

		@BeforeEach
		void setUp() throws Exception {
			// タスク登録上限を3に設定
			ReflectionTestUtils.setField(taskServiceImpl, "max_registration", 3);
		} 
		
		@ParameterizedTest
		@ValueSource(ints = {3, 4})
		void 登録できるタスク上限数を超えていた場合はBusinessExceptionを送出(int registered_number) {
			// SetUp		
			when(taskRepository.countByUserIdAndNotDeleted(user.getUserId())).thenReturn(registered_number);
			
			// Exercise, Verify
			assertThrows(BusinessException.class, () -> taskServiceImpl.save(taskForm, user));
			verify(taskRepository).countByUserIdAndNotDeleted(user.getUserId());
			verify(taskRepository, never()).save(task);
		}
		
		
		@ParameterizedTest
		@ValueSource(ints = {0, 1, 2})
		void 登録できるタスク上限数を超えていない場合はBusinessExceptionが送出されない(int registered_number) {
			// SetUp
			taskForm.setLoad(Load.HIGH.getCode());
			when(taskRepository.countByUserIdAndNotDeleted(user.getUserId())).thenReturn(registered_number);
			
			// Exercise, Verify
			try {
				taskServiceImpl.save(taskForm, user);
			} catch(BusinessException e) {
				fail();
			}
			
		}
	}
	
	@Nested
	class getTaskListメソッドの検証 {
	
		@Test
		void taskが存在しない場合はResouceNotFoundExceptionを送出する() {
			// SetUp
			when(taskRepository.myFindByUserId(user.getUserId())).thenReturn(null);
			
			// Exercise, Verify
			assertThrows(ResourceNotFoundException.class, () -> taskServiceImpl.getTaskList(user.getUserId()));
		}
		
		@Nested
		class taskが存在する場合 {
			
			@Test
			void 実行回数にnullが含まれていればSystemExceptionを送出する() {
				// SetUp
				when(taskRepository.myFindByUserId(user.getUserId())).thenReturn(taskList);
				when(taskExecutionHistoryRepository.countByTaskId(task.getTaskId())).thenReturn(null);
				
				//Exercise, Verify
				assertThrows(SystemException.class, () -> taskServiceImpl.getTaskList(user.getUserId()));
			}
			
			@ParameterizedTest
			@ValueSource(ints = {0, 1, 30, 9999})
			void 実行回数にnullが含まれていなければ例外を送出しない(int count) {
				// SetUp
				when(taskRepository.myFindByUserId(user.getUserId())).thenReturn(taskList);
				when(taskExecutionHistoryRepository.countByTaskId(task.getTaskId())).thenReturn(count);
				
				//Exercise, Verify
				try {
					taskServiceImpl.getTaskList(user.getUserId());
				} catch(ResourceNotFoundException e) {
					fail();
				} catch(SystemException e) {
					fail();
				}
			}			
		}
	}

	
	@Nested
	class deleteTaskメソッドの検証 {
		
		@Test
		void 選択したtaskが存在しない場合はResouceNotFoundExceptionを送出する() {
			// SetUp
			when(taskRepository.myfindByTaskId(task.getTaskId())).thenReturn(null);
						
			// Exercise, Verify
			assertThrows(ResourceNotFoundException.class, () -> taskServiceImpl.deleteTask(task.getTaskId()));
		}
		
		@Nested
		class 選択したtaskが存在する場合 {
			
			@Test
			void taskが既に削除されていればBusinessExceptionを送出する() {
				// SetUp
				task.setDeleted(Delete.DELETED);
				when(taskRepository.myfindByTaskId(task.getTaskId())).thenReturn(task);
				
				// Exercise, Verify
				assertThrows(BusinessException.class, () -> taskServiceImpl.deleteTask(task.getTaskId()));
				
			}
			
			@Test
			void taskが削除されていなければ例外は送出されない() {
				// SetUp
				task.setDeleted(Delete.VALID);
				when(taskRepository.myfindByTaskId(task.getTaskId())).thenReturn(task);
				
				// Exercise, Verify
				try {
					taskServiceImpl.deleteTask(task.getTaskId());
				} catch(ResourceNotFoundException e) {
					fail();
				} catch(BusinessException e) {
					fail();
				}
			}
				
		}
		
	}
	
	
	@Nested
	class getTaskメソッドの検証 {
	
		@Test
		void taskが存在しない場合はResouceNotFoundExceptionを送出する() {
			// SetUp
			when(taskRepository.myfindByTaskId(task.getTaskId())).thenReturn(null);
			
			// Exercise, Verify
			assertThrows(ResourceNotFoundException.class, () -> taskServiceImpl.getTask(task.getTaskId()));
		}
		
		@Test
		void taskが存在する場合は例外を送出しない() {
			// SetUp
			when(taskRepository.myfindByTaskId(task.getTaskId())).thenReturn(task);
						
			// Exercise, Verify
			try {
				taskServiceImpl.getTask(task.getTaskId());
			} catch(ResourceNotFoundException e) {
				fail();
			}
		}
	}

}
