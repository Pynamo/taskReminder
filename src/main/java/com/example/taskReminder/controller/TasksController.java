package com.example.taskReminder.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.core.ParameterizedTypeReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import com.example.taskReminder.common.Load;
import com.example.taskReminder.common.MessageAlertLevel;
import com.example.taskReminder.form.TaskForm;
import com.example.taskReminder.form.TaskFormList;
import com.example.taskReminder.form.ChartJsInputDataObject;
import com.example.taskReminder.mapper.TaskMapper;
import com.example.taskReminder.service.TaskExecuteService;
import com.example.taskReminder.service.TaskService;
import com.example.taskReminder.entity.Task;
import com.example.taskReminder.entity.UserInf;
import com.example.taskReminder.entity.TasksExecutionHistory;
import com.example.taskReminder.exception.BusinessException;
import com.example.taskReminder.exception.ResourceNotFoundException;
import com.example.taskReminder.exception.SystemException;

@Controller
public class TasksController {

	protected static Logger log = LoggerFactory.getLogger(TasksController.class);

	@Autowired
	private TaskService taskService;
	@Autowired
	TaskExecuteService taskExecuteService;
	@Autowired
	private MessageSource messages;

	/**
	 * タスク一覧表示
	 */
	@GetMapping
	public String list(Principal principal, Model model) {

		Authentication authentication = (Authentication) principal;
		UserInf user = (UserInf) authentication.getPrincipal();
		

		List<Task> tasks = new ArrayList<>();

		// タスク一覧を取得
		try {
			tasks = taskService.getTaskList(user.getUserId());

		} catch (ResourceNotFoundException e) {
			log.error("Task is not found");
			displayMessageForwardHelper(
					MessageAlertLevel.WARNING, 
					"タスクを登録しましょう", 
					model);
		}

		List<TaskForm> list = new ArrayList<>();
		list = TaskMapper.INSTANCE.tasksToTaskForm(tasks);
		
		
		// 各タスクの実行回数を取得
		try {
			list = taskService.getTaskNumberOfExecution(list);
		} catch(SystemException e) {
			log.error("Task is not found");
			displayMessageForwardHelper(
					MessageAlertLevel.WARNING, 
					"システム例外が発生しました", 
					model);
		}
		
		model.addAttribute("list", list);

		return "tasks/index";
	}

	
	/**
	 * タスク新規作成フォームの表示
	 */
	@GetMapping(value = "/create", params = "form")
	public String createForm(Model model) {

		TaskForm taskForm = new TaskForm();

		model.addAttribute("loadMst", Load.getLoadData());
		model.addAttribute("taskForm", taskForm);

		return "tasks/new";
	}

	
	/**
	 * タスク新規作成内容の確認
	 */
	@PostMapping(value = "/create", params = "confirm")
	public String createConfirm(
			@Validated TaskForm taskForm, 
			BindingResult result, 
			Model model) {

		if (result.hasErrors()) {
			model.addAttribute("loadMst", Load.getLoadData());
			model.addAttribute("taskForm", taskForm);
			return "tasks/new";
		}
		
		// 柴犬画像の取得（ランダム）
		String imgUrl = taskService.getSibaImageUrl();

		taskForm.setImgUrl(imgUrl);
		
		model.addAttribute("taskForm", taskForm);
		// 想定負荷コードが確認欄に表示されてしまうことを避けるため、名称に変換してから渡す
		model.addAttribute("loadName", Load.getValue(taskForm.getLoad()).getName());
		
		return "tasks/confirm";
	}

	/**
	 * 新規作成内容に修正がある時に前画面に戻る
	 */
	@PostMapping(value = "/create", params = "redo")
	public String createRedo(TaskForm taskForm, Model model) {

		model.addAttribute("loadMst", Load.getLoadData());
		model.addAttribute("taskForm", taskForm);

		return "tasks/new";
	}

	/**
	 * タスク作成
	 */
	@PostMapping(value = "/create")
	public String create(@Validated TaskForm taskForm, BindingResult bindingResult, Principal principal,
			RedirectAttributes redirAttrs, Model model) {

		Authentication authentication = (Authentication) principal;
		UserInf user = (UserInf) authentication.getPrincipal();
		
		try {
			taskService.save(taskForm, user);
		} catch (BusinessException e) {

			log.error("Task is full!");
			displayMessageRedirectHelper(
					MessageAlertLevel.ERROR, 
					"3個以上タスクを登録できません。追加したい場合は既存のタスクを削除しましょう",
					redirAttrs);

			return "redirect:/";
		}

		displayMessageRedirectHelper(
				MessageAlertLevel.SUCCESS, 
				"新しいタスクを登録しました。頑張って習慣化しましょう！", 
				redirAttrs);

		return "redirect:/";
	}

	/**
	 * 削除画面表示
	 */
	@GetMapping(value = "/delete", params = "form")
	public String deleteList(Principal principal, Model model) {

		Authentication authentication = (Authentication) principal;
		UserInf user = (UserInf) authentication.getPrincipal();

		List<Task> tasks = new ArrayList<>();

		try {
			tasks = taskService.getTaskList(user.getUserId());
		} catch (ResourceNotFoundException e) {
			log.error("Task is not found");
			displayMessageForwardHelper(
					MessageAlertLevel.WARNING, 
					"タスクが存在しません", 
					model);
		}

		TaskFormList taskFormList = new TaskFormList();
		List<TaskForm> list = new ArrayList<>();
		list = TaskMapper.INSTANCE.tasksToTaskForm(tasks);

		taskFormList.setTaskFormList(list);

		model.addAttribute("list", taskFormList);

		return "tasks/delete";
	}


	/**
	 * タスク削除
	 */
	@PostMapping(value = "/delete")
	public String deleteSelected(
			TaskFormList taskFormList, 
			Principal principal, 
			RedirectAttributes redirAttrs) {

		if (Objects.isNull(taskFormList.getTaskFormList())) {
			displayMessageRedirectHelper(MessageAlertLevel.WARNING, "タスクが存在しません", redirAttrs);
			return "redirect:/delete?form";
		}

		int counter = 0;

		// タスク一覧から選択済のタスクを削除する
		for (TaskForm taskForm : taskFormList.getTaskFormList()) {

			if (taskForm.isSelected()) {

				// 選択されているタスク数を数えるためのカウンター
				counter += 1;

				try {

					taskService.deleteTask(taskForm.getTaskId());

				} catch (ResourceNotFoundException e) {

					log.error("Task not found!");
					displayMessageRedirectHelper(MessageAlertLevel.ERROR, "削除する対象が見つかりません", redirAttrs);
					return "redirect:/delete?form";

				} catch (BusinessException e) {

					log.error("Task is deleted!");
					displayMessageRedirectHelper(MessageAlertLevel.ERROR, "対象は既に削除されています", redirAttrs);
					return "redirect:/delete?form";

				}
			}
		}
		
		if (counter == 0) {
			displayMessageRedirectHelper(MessageAlertLevel.WARNING, "削除するタスクを選択してください", redirAttrs);
		} else {
			displayMessageRedirectHelper(MessageAlertLevel.SUCCESS, "タスクを削除しました！さあ次は何をはじめますか？", redirAttrs);
		}

		return "redirect:/delete?form";
	}

	/**
	 * タスク更新画面表示
	 */
	@GetMapping(value = "/update")
	public String updateForm(
			@RequestParam("task_id") long taskId, 
			RedirectAttributes redirAttrs,
			Model model) {

		Task task = new Task();

		try {
			task = taskService.getTask(taskId);
		} catch (ResourceNotFoundException e) {
			log.error("Task is not found!");
			displayMessageRedirectHelper(MessageAlertLevel.ERROR, "対象が見つかりません", redirAttrs);
			return "redirect:/";
		}

		TaskForm taskForm = TaskMapper.INSTANCE.taskToForm(task);

		model.addAttribute("loadMst", Load.getLoadData());
		model.addAttribute("taskForm", taskForm);

		return "tasks/update"; 
	}
	
	// タスク更新
	@PostMapping(value = "/update")
	public String update(
			@Validated TaskForm taskForm, 
			BindingResult result,
			Principal principal,
			RedirectAttributes redirAttrs,
			Model model) {
		
		if (result.hasErrors()) {
			model.addAttribute("loadMst", Load.getLoadData());
			model.addAttribute("taskForm", taskForm);
			return "tasks/update";
		}
		
		Task task = new Task();

		try {
			task = taskService.getTask(taskForm.getTaskId());
		} catch (ResourceNotFoundException e) {
			log.error("Task is not found!");
			displayMessageRedirectHelper(
					MessageAlertLevel.ERROR, 
					"対象が見つかりません", 
					redirAttrs);
			
			return "redirect:/";
		}
		
		task.setName(taskForm.getName());
		task.setContent(taskForm.getContent());
		task.setLoad(Load.getValue(taskForm.getLoad()));
		task.setUpdatedAt(new Date());
		
		
		taskService.update(task);
		displayMessageRedirectHelper(
				MessageAlertLevel.SUCCESS, 
				"タスクを更新しました。新規一転頑張りましょう！", 
				redirAttrs);
		
		return "redirect:/";
	}

	/**
	 * タスク詳細画面表示（実行履歴をグラフ表示） 
	 */
	@GetMapping(value = "/detail")
	public String detail(
			@RequestParam("task_id") long taskId, 
			Model model) {

		Task task = new Task();

		try {
			task = taskService.getTask(taskId);
		} catch (ResourceNotFoundException e) {
			log.error("Task is not found!");
			displayMessageForwardHelper(MessageAlertLevel.ERROR, "対象が見つかりません", model);
		}

		
		// タイトルにタスク名を挿入する
		String title = messages.getMessage("A0006_title", new String[] { task.getName() }, Locale.JAPAN);
		model.addAttribute("title", title);

		
		// サブタイトルをタスクの負荷によって変更する
		String subtitle = "";

		if (task.getLoad().equals(Load.LOW)) {
			subtitle = messages.getMessage("A0006_subtitle_load_low", null, Locale.JAPAN);
		} else if (task.getLoad().equals(Load.MIDDLE)) {
			subtitle = messages.getMessage("A0006_subtitle_load_middle", null, Locale.JAPAN);
		} else {
			subtitle = messages.getMessage("A0006_subtitle_load_high", null, Locale.JAPAN);
		}
		model.addAttribute("subtitle", subtitle);

		/**
		 * タスク実行履歴をグラフ化するための入力データを準備する
		 */
		List<TasksExecutionHistory> list = new ArrayList<>();
		List<ChartJsInputDataObject> chartInputData = new ArrayList<>();

		try {
			list = taskExecuteService.getTaskExecuteHistory(taskId);
		} catch (BusinessException e) {
			log.debug("このタスクは過去に一度も実行されていません");
			displayMessageForwardHelper(
					MessageAlertLevel.WARNING, 
					"このタスクはまだ一度も実行されていないため実績は表示されません。", 
					model);

			model.addAttribute("chartInputData", chartInputData);

			return "tasks/detail";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		//　実行回数を積み上げるためのカウンター
		int counter = 0;
		
		for (TasksExecutionHistory data : list) {
			counter += 1;
			chartInputData.add(new ChartJsInputDataObject(sdf.format(data.getCreatedAt()), counter));
		}

		model.addAttribute("chartInputData", chartInputData);

		return "tasks/detail";
	}

	
	/*
	 * リダイレクト先にメッセージを表示するためのヘルパー関数
	 */
	private void displayMessageRedirectHelper(MessageAlertLevel level, String message, RedirectAttributes redirAttrs) {

		redirAttrs.addFlashAttribute("hasMessage", true); 
		redirAttrs.addFlashAttribute("class", level.getCode()); 
		redirAttrs.addFlashAttribute("message", message);

	}

	/*
	 * フォワード先にメッセージを表示するためのヘルパー関数
	 */
	private void displayMessageForwardHelper(MessageAlertLevel level, String message, Model model) {

		model.addAttribute("hasMessage", true);
		model.addAttribute("class", level.getCode());
		model.addAttribute("message", message);

	}

}
