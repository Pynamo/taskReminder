package com.example.taskReminder.service;

import java.util.List;

import com.example.taskReminder.entity.TasksExecutionHistory;
import com.example.taskReminder.exception.BusinessException;
import com.example.taskReminder.exception.SystemException;

public interface TaskExecuteService {

	void execute(Long taskId) throws SystemException, BusinessException;

	List<TasksExecutionHistory> getTaskExecuteHistory(long taskId) throws BusinessException;
}
