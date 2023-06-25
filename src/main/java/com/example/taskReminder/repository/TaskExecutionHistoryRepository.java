package com.example.taskReminder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.taskReminder.entity.Task;
import com.example.taskReminder.entity.TasksExecutionHistory;

@Repository
public interface TaskExecutionHistoryRepository extends JpaRepository <TasksExecutionHistory, Long> {

	Integer countByTaskId(Long taskId);
	List<TasksExecutionHistory> findCreatedAtByTaskIdOrderByCreatedAtDesc(Long taskId); 
}