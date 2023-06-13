package com.example.taskReminder.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.taskReminder.exception.BusinessException;
import com.example.taskReminder.exception.SystemException;
import com.example.taskReminder.service.TaskExecuteService;
import com.example.taskReminder.service.TaskService;
import com.example.taskReminder.common.MessageAlertLevel;
import com.example.taskReminder.common.MessageDestination;


@Controller
public class TaskExecutionsController {
	
	protected static Logger log = LoggerFactory.getLogger(TaskExecutionsController.class);

	@Autowired
	TaskService taskService;
	@Autowired
	TaskExecuteService taskExecuteService;

	// TODO メッセージ表示処理はHelper関数で行う（logはcatchの部分で出すのが正解）
	
	/**
	 * タスク実行処理
	 * タスクが既に実行済であればSystemException発生
	 */
	@PostMapping(value="/execute")
	public String execute(
			@RequestParam("task_id") long taskId, 
			RedirectAttributes redirAttrs,
			Model model) {
		
		try {
			taskExecuteService.execute(taskId);
			log.error("execute!");
			redirAttrs.addFlashAttribute("hasMessage", true);
			redirAttrs.addFlashAttribute("class", "alert-success");
			redirAttrs.addFlashAttribute("message", "実行しました");
		} catch(SystemException e) {
			log.error("Task is already executed!");
			redirAttrs.addFlashAttribute("hasMessage", true);
			redirAttrs.addFlashAttribute("class", "alert-danger");
			redirAttrs.addFlashAttribute("message", "システム例外");
		} catch(BusinessException e) {
			log.error("Task is already executed!");
			
			displayMessageRedirectHelper(MessageAlertLevel.ERROR, "システム例外", redirAttrs);
			//redirAttrs.addFlashAttribute("hasMessage", true);
			//redirAttrs.addFlashAttribute("class", "alert-danger");
			//redirAttrs.addFlashAttribute("message", "システム例外");
		}
		return "redirect:/";
	}
	
	
	private void displayMessageRedirectHelper(
			MessageAlertLevel level,
			String message,
			RedirectAttributes redirAttrs) {
		
		redirAttrs.addFlashAttribute("hasMessage", true);
		redirAttrs.addFlashAttribute("class", level.getCode());
		redirAttrs.addFlashAttribute("message", message);
	
	}
}
