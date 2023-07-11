package com.example.taskReminder.service;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.taskReminder.common.Status;
import com.example.taskReminder.entity.Task;
import com.example.taskReminder.entity.TasksExecutionHistory;
import com.example.taskReminder.exception.BusinessException;
import com.example.taskReminder.exception.SystemException;
import com.example.taskReminder.repository.TaskExecutionHistoryRepository;
import com.example.taskReminder.repository.TaskRepository;

@Service
public class TaskExecuteServiceImpl implements TaskExecuteService {
	
	protected static Logger log = LoggerFactory.getLogger(TaskExecuteServiceImpl.class);

	@Autowired
    TaskRepository taskRepository;
	@Autowired
	TaskExecutionHistoryRepository taskExecutionHistoryRepository;
	
	/**
	 * タスク実行処理
	 * 1. 実行したいタスクIDを受けてタスクの情報を取得
	 * 2. ステータスが実行済であればSystemExceptionを投げる
	 * 3. 未実行であれば実行済にし、タスクテーブルを更新
	 * 4. タスク実行履歴に保存
	 */
	@Override
	public void execute(Long taskId) throws BusinessException {
		
		Task task = taskRepository.myfindByTaskId(taskId);
		
		if(task.getStatus().equals(Status.EXECUTED)) {
			throw new BusinessException("");
		} 
		
		task.setStatus(Status.EXECUTED);
		taskRepository.save(task);
		
		TasksExecutionHistory tasksExecutionHistory = new TasksExecutionHistory();
		tasksExecutionHistory.setTaskId(taskId);
		taskExecutionHistoryRepository.save(tasksExecutionHistory);
		
	}

	@Override
	public List<TasksExecutionHistory> getTaskExecuteHistory(long taskId) throws BusinessException {
		
		List<TasksExecutionHistory> list = taskExecutionHistoryRepository.findCreatedAtByTaskIdOrderByCreatedAtDesc(taskId);
		
		if(list.size() == 0) {
			throw new BusinessException("");
		}
		
		return list;
	}
	
	
	
}
