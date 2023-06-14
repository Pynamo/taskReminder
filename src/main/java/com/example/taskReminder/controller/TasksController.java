package com.example.taskReminder.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import java.util.ArrayList;
import java.util.List;

import com.example.taskReminder.common.Load;
import com.example.taskReminder.common.MessageAlertLevel;
import com.example.taskReminder.form.TaskForm;
import com.example.taskReminder.mapper.TaskMapper;
import com.example.taskReminder.service.TaskService;
import com.example.taskReminder.entity.Task;
import com.example.taskReminder.entity.UserInf;
import com.example.taskReminder.exception.BusinessException;
import com.example.taskReminder.exception.ResourceNotFoundException;

//import jp.fintan.keel.spring.web.token.transaction.TransactionTokenCheck;
//import jp.fintan.keel.spring.web.token.transaction.TransactionTokenType;

@Controller
//@TransactionTokenCheck("transactionTokenCheck")
public class TasksController {
	
	protected static Logger log = LoggerFactory.getLogger(TasksController.class);
	
	
	// TODO 国際化やってみる
	// TODO トランザクショントークンチェックやってみる（その前にユーザー認証かも
	// TODO 単体テスト書いてみる
	// TODO CSRF対策やってみる
	// TODO XSS対策やってみる
	// TODO ロギングやってみる

	@Autowired
	private TaskService taskService;

	/**
	 * タスク一覧表示
	 * タスクが一つも登録されていない場合はResourceNotFoundException発生
	 */
	@GetMapping
	public String list(
			Principal principal,
			Model model) {
		
		Authentication authentication = (Authentication) principal;
        UserInf user = (UserInf) authentication.getPrincipal();
        
        Iterable<Task> tasks = new ArrayList<>();
        
        try {
        	tasks = taskService.getTaskList(user.getUserId());
        	
        } catch(ResourceNotFoundException e) {
        	// TODO 画面にメッセージを出力する
        }
        
        List<TaskForm> list = new ArrayList<>();
        list = TaskMapper.INSTANCE.tasksToTaskForm(tasks);
        model.addAttribute("list", list);
        
		return "tasks/index";
	}
	
	
	/**
	 * 新規作成フォーム（更新も兼用）
	 */
	@GetMapping(value="/create", params="form")
	public String createForm(Model model) {
		
		TaskForm taskForm = new TaskForm();
		
		model.addAttribute("loadMst", Load.getLoadData());
		model.addAttribute("taskForm", taskForm);
		
		return "tasks/new";
	}
	
	/**
	 * 新規作成内容の確認
	 */
	//@TransactionTokenCheck(type = TransactionTokenType.BEGIN)
	@PostMapping(value="/create", params="confirm")
	public String createConfirm(
			@Validated TaskForm taskForm,
			BindingResult result,
			Model model) {
		
		if(result.hasErrors()) {
			model.addAttribute("loadMst", Load.getLoadData());
			model.addAttribute("taskForm", taskForm);
			return "tasks/new";
		}
		model.addAttribute("taskForm", taskForm);

		// TODO セレクトボックスのコード値ではなく値を渡すにはどうすれば？
		return "tasks/confirm";		
	}
	
	/**
	 * 新規作成内容に修正がある時に前画面に戻る
	 */
	@PostMapping(value="/create", params="redo")
	public String createRedo(
			TaskForm taskForm,
			Model model) {
		
		model.addAttribute("loadMst", Load.getLoadData());
		model.addAttribute("taskForm", taskForm);
		
		return "tasks/new";
	}
	

	/**
	 * タスクを作成
	 * タスクが既に3つ以上登録されていた場合はBuisinessException発生
	 */
	//@TransactionTokenCheck(type = TransactionTokenType.IN)
	@PostMapping(value="/create")
	public String create(
			@Validated TaskForm taskForm,
			BindingResult bindingResult,
			Principal principal,
			RedirectAttributes redirAttrs,
			Model model) {
		
		Authentication authentication = (Authentication) principal;
        UserInf user = (UserInf) authentication.getPrincipal();
        
        try {
        	taskService.save(taskForm, user);
        } catch(BusinessException e) {
        	
        	log.error("Task is full!");
        	displayMessageRedirectHelper(
					MessageAlertLevel.ERROR, 
					"3個以上タスクを登録できません。追加したい場合は既存のタスクを削除しましょう", 
					redirAttrs);
	    	
	    	return "redirect:/";
        }
        
		return "redirect:/create?complete";		
	}
	
	/**
	 * 登録完了時の画面表示
	 */
	@GetMapping(value="/create", params="complete")
	public String createComplete() {

		return "tasks/complete";
	}
	
	/**
	 * 削除画面呼び出し
	 * タスクが一つも登録されていない時はResouceNotFoundException発生
	 */
	@GetMapping(value="/delete", params="form")
	public String deleteList(
			Principal principal,
			Model model) {
		
		Authentication authentication = (Authentication) principal;
        UserInf user = (UserInf) authentication.getPrincipal();
        
        Iterable<Task> tasks = new ArrayList<>();
        
        try {
        	tasks = taskService.getTaskList(user.getUserId());
        } catch(ResourceNotFoundException e) {
        	// TODO 削除対象が見つからない時はエラーメッセージ表示
        }
        
        List<TaskForm> list = new ArrayList<>();
        list = TaskMapper.INSTANCE.tasksToTaskForm(tasks);
        model.addAttribute("list", list);		
        
		return "tasks/delete";
	}
	
	/**
	 * 削除実行
	 * 削除対象が存在しない時はResouceNotFoundException発生
	 * 削除対象が既に削除されている時はSystemException発生
	 */
	@PostMapping(value="/delete")
	public String delete(
			@RequestParam("task_id") long taskId, 
			Principal principal,
			Model model) {
		
		try {
			taskService.deleteTask(taskId);
		} catch(ResourceNotFoundException e) {
			log.error("Task not found!");
			displayMessageForwardHelper(
					MessageAlertLevel.ERROR, 
					"削除する対象が見つかりません", 
					model);
		} catch(BusinessException e) {
			log.error("Task is deleted!");
			displayMessageForwardHelper(
					MessageAlertLevel.ERROR, 
					"対象は既に削除されています", 
					model);
		}
		
		return "redirect:/delete?form";
	}
	
	/**
	 * タスク内容の更新（タスクを取得し、新規作成時と同じhtmlのフォームに値を詰める）
	 * 対象のタスクが存在しない時はResourceNotFoundException発生
	 */
	@GetMapping(value="/task", params="update")
	public String updateForm(
			@RequestParam("task_id") long taskId, 
			Model model) {
		
		Task task = new Task();
		
		try {
			task = taskService.getTask(taskId);
		} catch(ResourceNotFoundException e) {
			log.error("Task is not found!");
			displayMessageForwardHelper(
					MessageAlertLevel.ERROR, 
					"対象が見つかりません", 
					model);
		}

		TaskForm taskForm = TaskMapper.INSTANCE.taskToForm(task);
		
		model.addAttribute("loadMst", Load.getLoadData());
		model.addAttribute("taskForm", taskForm);
		
		return "tasks/new";
	}
	
	/**
	 * タスク詳細画面表示（グラフ表示予定）
	 * 対象のタスクが存在しない時はResourceNotFoundException発生
	 */
	@GetMapping(value="/task", params="detail")
	public String detail(
			@RequestParam("task_id") long taskId, 
			Model model) {
		
		Task task = new Task();
		
		try {
			task = taskService.getTask(taskId);
		} catch(ResourceNotFoundException e) {
			log.error("Task is not found!");
			displayMessageForwardHelper(
					MessageAlertLevel.ERROR, 
					"対象が見つかりません", 
					model);
		}

		TaskForm taskForm = TaskMapper.INSTANCE.taskToForm(task);
		model.addAttribute("taskForm", taskForm);
		
		return "tasks/detail";
	}
	

	private void displayMessageRedirectHelper(
			MessageAlertLevel level,
			String message,
			RedirectAttributes redirAttrs) {
		
		redirAttrs.addFlashAttribute("hasMessage", true);
		redirAttrs.addFlashAttribute("class", level.getCode());
		redirAttrs.addFlashAttribute("message", message);
	
	}
	
	private void displayMessageForwardHelper(
			MessageAlertLevel level,
			String message,
			Model model) {
		
		model.addAttribute("hasMessage", true);
		model.addAttribute("class", level.getCode());
		model.addAttribute("message", message);
	
	}

}
