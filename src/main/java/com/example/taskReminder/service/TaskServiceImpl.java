package com.example.taskReminder.service;


import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.ArrayList;

import com.example.taskReminder.common.Delete;
import com.example.taskReminder.entity.Task;
import com.example.taskReminder.entity.UserInf;
import com.example.taskReminder.exception.BusinessException;
import com.example.taskReminder.exception.ResourceNotFoundException;
import com.example.taskReminder.exception.SystemException;
import com.example.taskReminder.form.TaskForm;
import com.example.taskReminder.mapper.TaskMapper;
import com.example.taskReminder.repository.TaskExecutionHistoryRepository;
import com.example.taskReminder.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
public class TaskServiceImpl implements TaskService {
	
	protected static Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

	@Autowired
    TaskRepository taskRepository;
	@Autowired
    TaskExecutionHistoryRepository taskExecutionHistoryRepository;
	@Autowired
    private RestTemplate restTemplate;
	
	@Value("${max.registration:3}") 
	private int max_registration;
	
	@Value("${sibe.url:}")
	private String sibe_image_request_url;
	@Value("${recovery.url:}")
	private String recovery_sibe_image_url;
	@Value("${retry.max.count:3}")
	private int retryMaxCount;

	/**
	 * タスク登録処理
	 * 登録済のタスクが3つ以上であればBusinessExceptionを投げる
	 */
	@Override
	public void save(TaskForm taskForm, UserInf user) throws BusinessException {
		
		int registeredCounter =  taskRepository.countByUserIdAndNotDeleted(user.getUserId());

		if(registeredCounter >= max_registration) {
			// TODO エラーメッセージのタスク上限を変数化する
			throw new BusinessException("3個以上タスクを登録できません。追加したい場合は既存のタスクを削除しましょう");
		}
		Task task = TaskMapper.INSTANCE.formToTask(taskForm, user);
		taskRepository.save(task);
	}

	/**
	 * 登録済タスク一覧取得
	 * タスクがひとつも登録されていない場合はResourceNotFoundExceptionを投げる
	 * @throws SystemException 
	 */
	@Override
	public List<Task> getTaskList(Long userId) throws ResourceNotFoundException, SystemException {
		
		List<Task> tasks = taskRepository.myFindByUserId(userId);
		if(Objects.isNull(tasks)) {
			throw new ResourceNotFoundException("Task not found!");
		}
		
		List<Task> list = new ArrayList<Task>();
		
		// タスク実行履歴テーブルから過去の実行回数を取得し、nullでなければTaskエンティティに追加する
		for(Task data : tasks) {
			
			int numberOfExecution = 0;
			
			try {
				numberOfExecution = taskExecutionHistoryRepository.countByTaskId(data.getTaskId());
			} catch(NullPointerException e) {
				throw new SystemException("");
			}
			
			data.setNumberOfExecution(numberOfExecution);
			
			list.add(data);
		}
		
		return list;
	}

	/**
	 * タスク削除処理
	 * タスクが見つからなければResourceNotFoundExceptionを投げる
	 * タスクが既に削除されていればSystemExceptionを投げる
	 * @throws BusinessException 
	 */
	@Override
	public void deleteTask(Long taskId) throws ResourceNotFoundException, BusinessException {
		
		Task task = taskRepository.myfindByTaskId(taskId);
		
		if(Objects.isNull(task)) {
			throw new ResourceNotFoundException("Task not found!");
		}
		
		if(task.getDeleted().equals(Delete.DELETED)) {
			throw new BusinessException("Task is deleted!");
		}
		task.setDeleted(Delete.DELETED);
		taskRepository.save(task);	
	}

	
	/**
	 * タスク取得（1個)
	 * 登録済のタスクが見つからなければResourceNotFoundExceptionを投げる
	 */
	@Override
	public Task getTask(Long taskId) throws ResourceNotFoundException {
		
		Task task = taskRepository.myfindByTaskId(taskId);
		if(Objects.isNull(task)) {
			throw new ResourceNotFoundException("Task not found!");
		}
		return task;
	}

	/**
	 * sibe.onlineのAPI経由で柴犬画像を取得する
	 */
	@Override
	public String getSibaImageUrl() {
		
		int retryCount = 0;
		
		RequestEntity<Void> requestEntity = RequestEntity.get(sibe_image_request_url).build();
		List<String> imgUrl = new ArrayList<>();

		while (true) {

			// リクエストの送信
			ResponseEntity<List<String>> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<String>>(){});

		    if (response.getStatusCode() == HttpStatus.OK) {
		    	imgUrl = response.getBody();
		        break;  
		    } 
		    
		    if(retryCount == retryMaxCount) {
		    	log.error("柴犬画像を取得できませんでした。");
		    	break;
		    }
		    
		    retryCount++;
		}
		
		if(imgUrl.size() == 0) {
			imgUrl.add(recovery_sibe_image_url);
		}


		return imgUrl.get(0);
	}
}
