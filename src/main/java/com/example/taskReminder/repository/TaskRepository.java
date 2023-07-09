package com.example.taskReminder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

import com.example.taskReminder.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository <Task, Long> {

	@Query(value = "select * from tasks "
			+ "where user_id = ?1 and deleted = '0' order by task_id;",
			nativeQuery = true)
	List<Task> myFindByUserId(Long userId);

	@Query(value = "select count(*) from tasks "
			+ "where user_id = ?1 and deleted = '0';",
			nativeQuery = true)
	Integer countByUserIdAndNotDeleted(Long userId);

	@Query(value = "select * from tasks "
			+ "where task_id = ?1 and deleted = '0';",
			nativeQuery = true)
	Task myfindByTaskId(Long taskId);

}