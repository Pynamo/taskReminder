package com.example.taskReminder.mapper;

import java.util.List;
import java.util.ArrayList;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.taskReminder.common.Delete;
import com.example.taskReminder.common.Load;
import com.example.taskReminder.common.Status;

import com.example.taskReminder.entity.Task;
import com.example.taskReminder.entity.UserInf;
import com.example.taskReminder.form.TaskForm;

@Mapper
public interface TaskMapper {
	
	TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);
	
	
    default Task formToTask(TaskForm taskForm, UserInf user) {
        Task task = new Task();
        //task.setLoad(Load.getValue(taskForm.getLoad()));
        task.setTaskId(taskForm.getTaskId());
        task.setName(taskForm.getName());
        task.setLoad(Load.getValue(taskForm.getLoad()));
        task.setContent(taskForm.getContent());
        task.setStatus(Status.NON_EXECTED);
        task.setDeleted(Delete.VALID);
        task.setUserId(user.getUserId());
        return task;
    }
    
    default List<TaskForm> tasksToTaskForm(Iterable<Task> tasks) {
    	
    	List<TaskForm> list = new ArrayList<>();
    	
    	for (Task entity : tasks) {
    		TaskForm form = new TaskForm(); 
    		form.setTaskId(entity.getTaskId());
    		form.setName(entity.getName());
    		form.setContent(entity.getContent());
    		form.setLoad(entity.getLoad().getName());
    		form.setNumberOfExecution(entity.getNumberOfExecution());
    		form.setStatus(entity.getStatus().getCode());
    		form.setUserId(entity.getUserId());
    		list.add(form);
    	}
    	return list;
    }
    
    default TaskForm taskToForm(Task task) {
    	TaskForm form = new TaskForm();
    	
    	form.setTaskId(task.getTaskId());
    	form.setName(task.getName());
    	form.setContent(task.getContent());
    	form.setLoad(task.getLoad().getCode());
    	
    	return form;
    }
}
