package com.example.taskReminder.service;

import java.util.List;

import com.example.taskReminder.entity.Task;
import com.example.taskReminder.entity.UserInf;
import com.example.taskReminder.exception.BusinessException;
import com.example.taskReminder.exception.ResourceNotFoundException;
import com.example.taskReminder.exception.SystemException;
import com.example.taskReminder.form.TaskForm;

public interface TaskService {

	void save(TaskForm taskForm, UserInf user) throws BusinessException;
	
	void update(Task task);

	List<Task> getTaskList(Long userId) throws ResourceNotFoundException;
	
	List<TaskForm> getTaskNumberOfExecution(List<TaskForm> tasks) throws SystemException;

	void deleteTask(Long taskId) throws ResourceNotFoundException, BusinessException;

	Task getTask(Long taskId) throws ResourceNotFoundException;

	String getSibaImageUrl();

}
