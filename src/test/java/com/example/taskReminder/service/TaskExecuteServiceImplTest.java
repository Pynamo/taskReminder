package com.example.taskReminder.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.example.taskReminder.common.Status;
import com.example.taskReminder.entity.Task;
import com.example.taskReminder.entity.TasksExecutionHistory;
import com.example.taskReminder.exception.BusinessException;
import com.example.taskReminder.repository.TaskExecutionHistoryRepository;
import com.example.taskReminder.repository.TaskRepository;


public class TaskExecuteServiceImplTest {

	@Mock
	TaskRepository taskRepository;
	@Mock
	TaskExecutionHistoryRepository taskExecutionHistoryRepository;
	
	@InjectMocks
	TaskExecuteServiceImpl taskExecuteServiceImpl;
	
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
	class executeメソッドの検証 {
		
		@Test
		void タスクのstatusがEXECUTEDなことによるBusinessExceptionの送出() throws Exception {
			
			Long taskId = 1L;
			Task task = new Task();
			task.setStatus(Status.EXECUTED);
			
			when(taskRepository.myfindByTaskId(taskId)).thenReturn(task);
			
			assertThrows(BusinessException.class, () -> taskExecuteServiceImpl.execute(taskId));		
			
		}
		
		@Test
		void タスク実行成功() throws Exception {

			Long taskId = 1L;
			Task task = new Task();
			task.setTaskId(taskId);
			TasksExecutionHistory tasksExecutionHistory = new TasksExecutionHistory();
			tasksExecutionHistory.setTaskId(taskId);
			
			task.setStatus(Status.NON_EXECTED);
			
			when(taskRepository.myfindByTaskId(taskId)).thenReturn(task);
			when(taskRepository.save(task)).thenReturn(null);
			when(taskExecutionHistoryRepository.save(tasksExecutionHistory)).thenReturn(null);
			
			taskExecuteServiceImpl.execute(taskId);
			
			Mockito.verify(taskExecutionHistoryRepository, Mockito.times(1)).save(tasksExecutionHistory);
			
			
		}
		
	}
	
	@Nested
	class getTaskExecuteHistoryメソッドの検証 {
		
		@Test
		void タスクが過去に一度も実行されていない時にBusinessException送出() throws Exception {
			
			Long taskId = 1L;
			
			List<TasksExecutionHistory> list = new ArrayList<>();

			when(taskExecutionHistoryRepository.findCreatedAtByTaskIdOrderByCreatedAtDesc(taskId)).thenReturn(list);
			
			assertThrows(BusinessException.class, () -> taskExecuteServiceImpl.getTaskExecuteHistory(taskId));		
		}
		
		@Test
		void タスク実行履歴取得成功() throws Exception {
			
			Long taskId = 1L;
			
			TasksExecutionHistory tasksExecutionHistory = new TasksExecutionHistory();
			List<TasksExecutionHistory> list = new ArrayList<>();
			list.add(tasksExecutionHistory);
			
			when(taskExecutionHistoryRepository.findCreatedAtByTaskIdOrderByCreatedAtDesc(taskId)).thenReturn(list);
			
			try {
				taskExecuteServiceImpl.getTaskExecuteHistory(taskId);
			} catch(BusinessException e) {
				fail();
			}
		}
	
	}
		
}
