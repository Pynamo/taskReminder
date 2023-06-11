package com.example.taskReminder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.taskReminder.entity.Task;
import com.example.taskReminder.entity.TasksExecutionHistory;

@Repository
public interface TaskExecutionHistoryRepository extends JpaRepository <TasksExecutionHistory, Long> {


}