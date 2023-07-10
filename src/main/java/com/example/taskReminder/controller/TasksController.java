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

	// TODO 国際化やってみる
	// TODO トランザクショントークンチェックやってみる（その前にユーザー認証かも
	// TODO 単体テスト書いてみる
	// TODO CSRF対策やってみる
	// TODO XSS対策やってみる
	// TODO ロギングやってみる

	@Autowired
	private TaskService taskService;
	@Autowired
	TaskExecuteService taskExecuteService;
	@Autowired
	private MessageSource messages;


	/**
	 * タスク一覧表示 タスクが一つも登録されていない場合はResourceNotFoundException発生
	 */
	@GetMapping
	public String list(Principal principal, Model model) {

		Authentication authentication = (Authentication) principal;
		UserInf user = (UserInf) authentication.getPrincipal();

		List<Task> tasks = new ArrayList<>();

		try {
			tasks = taskService.getTaskList(user.getUserId());

		} catch (ResourceNotFoundException e) {
			// TODO 画面にメッセージを出力する
		}

		List<TaskForm> list = new ArrayList<>();
		list = TaskMapper.INSTANCE.tasksToTaskForm(tasks);
		
		try {
			list = taskService.getTaskNumberOfExecution(list);
		} catch(SystemException e) {
			// TODO システム例外IDを画面に表示する
		}
		
		
		model.addAttribute("list", list);

		return "tasks/index";
	}

	/**
	 * 新規作成フォーム（更新も兼用）
	 */
	@GetMapping(value = "/create", params = "form")
	public String createForm(Model model) {

		TaskForm taskForm = new TaskForm();

		model.addAttribute("loadMst", Load.getLoadData());
		model.addAttribute("taskForm", taskForm);

		return "tasks/new";
	}

	/**
	 * 新規作成内容の確認
	 */
	@PostMapping(value = "/create", params = "confirm")
	public String createConfirm(@Validated TaskForm taskForm, BindingResult result, Model model) {

		if (result.hasErrors()) {
			model.addAttribute("loadMst", Load.getLoadData());
			model.addAttribute("taskForm", taskForm);
			return "tasks/new";
		}
		
		String imgUrl = taskService.getSibaImageUrl();

		taskForm.setImgUrl(imgUrl);
		
		model.addAttribute("taskForm", taskForm);

		// TODO セレクトボックスのコード値ではなく値を渡すにはどうすれば？
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
	 * タスクを作成 タスクが既に3つ以上登録されていた場合はBuisinessException発生
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
					"3個以上タスクを登録できません。追加したい場合は既存のタスクを削除しましょう", // TODO
					redirAttrs);

			return "redirect:/";
		}

		displayMessageRedirectHelper(MessageAlertLevel.SUCCESS, "新しいタスクを登録しました。頑張って習慣化しましょう！", redirAttrs);

		return "redirect:/";
	}

	/**
	 * 削除画面呼び出し タスクが一つも登録されていない時はResouceNotFoundException発生
	 */
	@GetMapping(value = "/delete", params = "form")
	public String deleteList(Principal principal, Model model) {

		Authentication authentication = (Authentication) principal;
		UserInf user = (UserInf) authentication.getPrincipal();

		List<Task> tasks = new ArrayList<>();

		try {
			tasks = taskService.getTaskList(user.getUserId());
		} catch (ResourceNotFoundException e) {
			// TODO 削除対象が見つからない時はエラーメッセージ表示
		}

		TaskFormList taskFormList = new TaskFormList();
		List<TaskForm> list = new ArrayList<>();
		list = TaskMapper.INSTANCE.tasksToTaskForm(tasks);

		taskFormList.setTaskFormList(list);

		model.addAttribute("list", taskFormList);

		return "tasks/delete";
	}

	/**
	 * 削除実行 削除対象が存在しない時はResouceNotFoundException発生 削除対象が既に削除されている時はSystemException発生
	 */
	// @TransactionTokenCheck
	/*
	 * @PostMapping(value="/delete") public String delete(
	 * 
	 * @RequestParam("task_id") long taskId, Principal principal, Model model) {
	 * 
	 * try { taskService.deleteTask(taskId); } catch(ResourceNotFoundException e) {
	 * log.error("Task not found!"); displayMessageForwardHelper(
	 * MessageAlertLevel.ERROR, "削除する対象が見つかりません", model); } catch(BusinessException
	 * e) { log.error("Task is deleted!"); displayMessageForwardHelper(
	 * MessageAlertLevel.ERROR, "対象は既に削除されています", model); }
	 * 
	 * return "redirect:/delete?form"; }
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

		for (TaskForm taskForm : taskFormList.getTaskFormList()) {

			if (taskForm.isSelected()) {

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
	 * タスク内容の更新（タスクを取得し、新規作成時と同じhtmlのフォームに値を詰める）
	 * 対象のタスクが存在しない時はResourceNotFoundException発生
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
		displayMessageRedirectHelper(MessageAlertLevel.SUCCESS, "タスクを更新しました。新規一転頑張りましょう！", redirAttrs);
		
		
		return "redirect:/";
	}

	/**
	 * タスク詳細画面表示（実行履歴をグラフ表示） 対象のタスクが存在しない時はResourceNotFoundException発生
	 */
	@GetMapping(value = "/detail")
	public String detail(@RequestParam("task_id") long taskId, Model model) {

		Task task = new Task();

		try {
			task = taskService.getTask(taskId);
		} catch (ResourceNotFoundException e) {
			log.error("Task is not found!");
			displayMessageForwardHelper(MessageAlertLevel.ERROR, "対象が見つかりません", model);
		}

		/**
		 * タイトルにタスク名を挿入する
		 */
		String title = messages.getMessage("A0006_title", new String[] { task.getName() }, Locale.JAPAN);
		model.addAttribute("title", title);

		/**
		 * サブタイトルをタスクの負荷によって変更する
		 */
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
			displayMessageForwardHelper(MessageAlertLevel.WARNING, "このタスクはまだ一度も実行されていないため実績は表示されません。", model);

			model.addAttribute("chartInputData", chartInputData);

			return "tasks/detail";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		for (TasksExecutionHistory data : list) {
			chartInputData.add(new ChartJsInputDataObject(sdf.format(data.getCreatedAt()), 1));
		}

		model.addAttribute("chartInputData", chartInputData);

		return "tasks/detail";
	}

	private void displayMessageRedirectHelper(MessageAlertLevel level, String message, RedirectAttributes redirAttrs) {

		redirAttrs.addFlashAttribute("hasMessage", true); 
		redirAttrs.addFlashAttribute("class", level.getCode()); 
		redirAttrs.addFlashAttribute("message", message);

	}

	private void displayMessageForwardHelper(MessageAlertLevel level, String message, Model model) {

		model.addAttribute("hasMessage", true);
		model.addAttribute("class", level.getCode());
		model.addAttribute("message", message);

	}

}
