package com.example.taskReminder.service;

import com.example.taskReminder.entity.Task;
import com.example.taskReminder.entity.UserInf;
import com.example.taskReminder.exception.BusinessException;
import com.example.taskReminder.exception.ResourceNotFoundException;
import com.example.taskReminder.exception.SystemException;
import com.example.taskReminder.form.TaskForm;

public interface TaskService {

	// TODO 返り値は何が正解？
	void save(TaskForm taskForm, UserInf user) throws BusinessException;

	Iterable<Task> getTaskList(Long userId) throws ResourceNotFoundException;

	// TODO 返り値の型は何が正解？
	void deleteTask(Long taskId) throws ResourceNotFoundException, SystemException;

	Task getTask(Long taskId) throws ResourceNotFoundException;

}
