package com.example.taskReminder.service;


import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.taskReminder.common.Delete;
import com.example.taskReminder.entity.Task;
import com.example.taskReminder.entity.UserInf;
import com.example.taskReminder.exception.BusinessException;
import com.example.taskReminder.exception.ResourceNotFoundException;
import com.example.taskReminder.form.TaskForm;
import com.example.taskReminder.mapper.TaskMapper;
import com.example.taskReminder.repository.TaskRepository;

@Service
public class TaskServiceImpl implements TaskService {
	
	protected static Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

	@Autowired
    TaskRepository taskRepository;
	
	// TODO 最大値の範囲を指定したい場合はどうすれば？
	// TODO 書き換えられてしまうのでは？
	@Value("${max.registration:5}")
	private int max_registration;

	/**
	 * タスク登録処理
	 * 登録済のタスクが3つ以上であればBusinessExceptionを投げる
	 */
	@Override
	public void save(TaskForm taskForm, UserInf user) throws BusinessException {
		
		int registeredCounter =  taskRepository.countByUserIdAndNotDeleted(user.getUserId());

		if(registeredCounter >= max_registration) {
			// TODO エラーメッセージのタスク上限を変数化する
			throw new BusinessException("3個以上タスクを登録できません。追加したい場合は既存のタスクを削除しましょう");
		}
		Task task = TaskMapper.INSTANCE.formToTask(taskForm, user);
		taskRepository.save(task);
	}

	/**
	 * 登録済タスク一覧取得
	 * タスクがひとつも登録されていない場合はResourceNotFoundExceptionを投げる
	 * TODO 型をListに変更する
	 */
	@Override
	public Iterable<Task> getTaskList(Long userId) throws ResourceNotFoundException {
		
		Iterable<Task> tasks = taskRepository.myFindByUserId(userId);
		if(Objects.isNull(tasks)) {
			throw new ResourceNotFoundException("Task not found!");
		}
		return tasks;
	}

	/**
	 * タスク削除処理
	 * タスクが見つからなければResourceNotFoundExceptionを投げる
	 * タスクが既に削除されていればSystemExceptionを投げる
	 * @throws BusinessException 
	 */
	@Override
	public void deleteTask(Long taskId) throws ResourceNotFoundException, BusinessException {
		
		Task task = taskRepository.myfindByTaskId(taskId);
		
		if(Objects.isNull(task)) {
			throw new ResourceNotFoundException("Task not found!");
		}
		
		if(task.getDeleted().equals(Delete.DELETED)) {
			throw new BusinessException("Task is deleted!");
		}
		task.setDeleted(Delete.DELETED);
		taskRepository.save(task);	
	}

	
	/**
	 * タスク取得（1個)
	 * 登録済のタスクが見つからなければResourceNotFoundExceptionを投げる
	 */
	@Override
	public Task getTask(Long taskId) throws ResourceNotFoundException {
		
		Task task = taskRepository.myfindByTaskId(taskId);
		if(Objects.isNull(task)) {
			throw new ResourceNotFoundException("Task not found!");
		}
		return task;
	}
}
