package com.example.taskReminder.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.taskReminder.entity.Task;
import com.example.taskReminder.entity.UserInf;
import com.example.taskReminder.exception.BusinessException;
import com.example.taskReminder.exception.ResourceNotFoundException;
import com.example.taskReminder.exception.SystemException;
import com.example.taskReminder.form.TaskForm;
import com.example.taskReminder.mapper.TaskMapper;
import com.example.taskReminder.repository.TaskRepository;

@Service
public class TaskServiceImpl implements TaskService {
	
	protected static Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

	@Autowired
    TaskRepository taskRepository;

	/**
	 * タスク登録処理
	 * 登録済のタスクが3つ以上であればBusinessExceptionを投げる
	 * TODO 返り値なしで大丈夫？
	 */
	@Override
	public void save(TaskForm taskForm, UserInf user) throws BusinessException {
		
		int registeredCounter =  taskRepository.countByUserIdAndNotDeleted(user.getUserId());
		
		if(registeredCounter >= 3) {
			throw new BusinessException("3個以上タスクを登録できません。追加したい場合は既存のタスクを削除しましょう");
		}

		Task task = TaskMapper.INSTANCE.formToTask(taskForm, user);
		taskRepository.save(task);
	}

	/**
	 * 登録済タスク一覧取得
	 * タスクがひとつも登録されていない場合はResourceNotFoundExceptionを投げる
	 * TODO 型はIterableでいい？Listと何が違う？
	 * TODO 比較にnull使うのはダメ？
	 */
	@Override
	public Iterable<Task> getTaskList(Long userId) throws ResourceNotFoundException {
		
		Iterable<Task> tasks = taskRepository.myFindByUserId(userId);
		if(tasks == null) {
			throw new ResourceNotFoundException("Task not found!");
		}
		return tasks;
	}

	/**
	 * タスク削除処理
	 * タスクが見つからなければResourceNotFoundExceptionを投げる
	 * タスクが既に削除されていればSystemExceptionを投げる
	 * TODO 削除フラグ直書きだがenumがいい？
	 * TODO 返り値ないが大丈夫？
	 * TODO 比較にnull使うのはダメ？
	 */
	@Override
	public void deleteTask(Long taskId) throws ResourceNotFoundException, SystemException {
		
		Task task = taskRepository.myfindByTaskId(taskId);
		
		if(task == null) {
			throw new ResourceNotFoundException("Task not found!");
		}
		if(task.getDeleted() != null) {
			throw new SystemException("Task is deleted!");
		}
		task.setDeleted("1");
		taskRepository.save(task);	
	}

	
	/**
	 * タスク取得（1個)
	 * 登録済のタスクが見つからなければResourceNotFoundExceptionを投げる
	 * TODO 比較にnull使うのはダメ？
	 */
	@Override
	public Task getTask(Long taskId) throws ResourceNotFoundException {
		
		Task task = taskRepository.myfindByTaskId(taskId);
		if(task == null) {
			throw new ResourceNotFoundException("Task not found!");
		}
		return task;
	}
}
