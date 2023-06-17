package com.example.taskReminder.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.taskReminder.common.Load;
import com.example.taskReminder.entity.Task;
import com.example.taskReminder.entity.UserInf;
import com.example.taskReminder.entity.User;
import com.example.taskReminder.exception.BusinessException;
import com.example.taskReminder.form.TaskForm;
import com.example.taskReminder.repository.TaskRepository;

public class TaskServiceImplTest {

	/**
	 * 主な検証方法
	 * ・異常値テスト
	 * ・境界値テスト
	 * ・条件網羅テスト
	 */
	
	@Mock
	TaskRepository taskRepository;
	@InjectMocks
	TaskServiceImpl taskServiceImpl;
	private AutoCloseable closeable;

	// 各テストの前に実行
	@BeforeEach
	public void openMocks() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	// 各テストの後に実行
	@AfterEach
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	void タスク登録時に登録数上限を超えていた場合にビジネス例外が発生することを確認() {
		
		TaskForm taskForm = new TaskForm();
		Task task = new Task();
		UserInf user = new User();
		
		// タスク登録数として4を返却
		when(taskRepository.countByUserIdAndNotDeleted(user.getUserId())).thenReturn(4);
		// ビジネス例外がThrowされることを確認する
		assertThrows(BusinessException.class, () -> taskServiceImpl.save(taskForm, user));
		// タスク登録数確認処理が呼び出されたことを確認する
		verify(taskRepository).countByUserIdAndNotDeleted(user.getUserId());
		// タスク保存処理が呼び出されていないことを確認する
		verify(taskRepository, never()).save(task);
		// ThenReturnの設定をクリア 
		reset(taskRepository);
	}

	@Test
	void タスク登録成功() {
		
		TaskForm taskForm = new TaskForm();
		
		taskForm.setName("task_name");
		taskForm.setContent("task_content");
		taskForm.setLoad(Load.HIGH.getCode());
		
		Task task = new Task();
		
		task.setTaskId((long)1);
		task.setName("task_name");
		task.setContent("task_content");
		task.setLoad(Load.HIGH);
		
		UserInf user = new User();
		
		// タスク登録数として2を返却
		when(taskRepository.countByUserIdAndNotDeleted(user.getUserId())).thenReturn(2);
		when(taskRepository.save(task)).thenReturn(null);
		// タスク登録処理呼び出し
		
		try {
			taskServiceImpl.save(taskForm, user);
		} catch(BusinessException e) {
			fail();
		}
		
		// タスク登録数確認処理が呼び出されたことを確認する
		//verify(taskRepository).countByUserIdAndNotDeleted(user.getUserId());
		// タスク保存処理が呼び出されたことを確認する
		//verify(taskRepository).save(task);
		// ThenReturnの設定をクリア 
		reset(taskRepository);
	}
}


/*
@Override
public void save(TaskForm taskForm, UserInf user) throws BusinessException {
	
	int registeredCounter =  taskRepository.countByUserIdAndNotDeleted(user.getUserId());
	
	// TODO 定数はプロパティ上で管理する
	if(registeredCounter >= 3) {
		throw new BusinessException("3個以上タスクを登録できません。追加したい場合は既存のタスクを削除しましょう");
	}

	Task task = TaskMapper.INSTANCE.formToTask(taskForm, user);
	taskRepository.save(task);
}
*/