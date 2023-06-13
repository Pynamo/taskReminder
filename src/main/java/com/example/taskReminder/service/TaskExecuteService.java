package com.example.taskReminder.service;

import com.example.taskReminder.exception.BusinessException;
import com.example.taskReminder.exception.SystemException;

public interface TaskExecuteService {

	void execute(Long taskId) throws SystemException, BusinessException;
}
