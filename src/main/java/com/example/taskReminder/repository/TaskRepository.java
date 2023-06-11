package com.example.taskReminder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.taskReminder.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository <Task, Long> {

	// 検索条件にnullを含めるのはOK？
	@Query(value = "select * from tasks "
			+ "where user_id = ?1 and deleted is null;",
			nativeQuery = true)
	Iterable<Task> myFindByUserId(Long userId);

	@Query(value = "select count(*) from tasks "
			+ "where user_id = ?1 and deleted is null;",
			nativeQuery = true)
	Integer countByUserIdAndNotDeleted(Long userId);

	@Query(value = "select * from tasks "
			+ "where task_id = ?1 and deleted is null;",
			nativeQuery = true)
	Task myfindByTaskId(Long taskId);

}