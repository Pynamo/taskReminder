package com.example.taskReminder.repository;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import com.example.taskReminder.entity.Task;


@SpringBootTest
@Transactional
@Sql("classpath:sql/taskRepositoryTestScheme.sql")
public class TaskRepositoryTest {


    @Autowired
    private TaskRepository repository;
   
    @Test
	void myFindByUserIdメソッドの検証() {
    	
    	// UserId=11の時、TaskId=1のタスクが1つ返ることを確認する（deleted='1'は検索対象外とすること）
    	List<Task> list = repository.myFindByUserId(11L);
    	assertThat(list.size()).isEqualTo(1);
    	assertThat(list.get(0).getTaskId()).isEqualTo(1L);
    	
    	// UserId=22の時、TaskId=3のタスクが1つ返ることを確認する（deleted='1'は検索対象外とすること）
    	list = repository.myFindByUserId(22L);
    	assertThat(list.size()).isEqualTo(1);
    	assertThat(list.get(0).getTaskId()).isEqualTo(3L);
    	
    	// UserId=33の時、TaskId=5,6のタスクが2つ返ること、その順番が昇順であることを確認する（deleted='1'は検索対象外とすること）
    	list = repository.myFindByUserId(33L);
    	assertThat(list.size()).isEqualTo(2);
    	assertThat(list.get(0).getTaskId()).isEqualTo(5L);
    	assertThat(list.get(1).getTaskId()).isEqualTo(6L);
    	
    	// 存在しないUserIdの時、リストの要素が0であることを確認する
    	list = repository.myFindByUserId(99L);
    	assertThat(list.size()).isEqualTo(0);
    	
    }
    
    @Test
    void countByUserIdAndNotDeletedメソッドの検証() {
    	
    	// UserId=11のタスクが1件登録されていることを確認する（deleted='1'は検索対象外とすること）
    	int cnt = repository.countByUserIdAndNotDeleted(11L);
    	assertThat(cnt).isEqualTo(1);
    	
    	// UserId=33のタスクが2件登録されていることを確認する（deleted='1'は検索対象外とすること）
    	cnt = repository.countByUserIdAndNotDeleted(33L);
    	assertThat(cnt).isEqualTo(2);
    	
    	// UserId=99のタスクが登録されていないことを確認する（deleted='1'は検索対象外とすること）
    	cnt = repository.countByUserIdAndNotDeleted(99L);
    	assertThat(cnt).isEqualTo(0);
    }
    

    @Test
    void myfindByTaskIdの検証() {
    	
    	// TaskId=1のタスクを検索した時にTaskId=1のタスクが返ることを確認する（deleted='1'は検索対象外とすること）
    	Task task = repository.myfindByTaskId(1L);
    	assertThat(task.getTaskId()).isEqualTo(1L);
    	
    	// TaskId=2のタスクを検索した時にタスクが存在しないことを確認する（deleted='1'は検索対象外とすること）
    	task = repository.myfindByTaskId(2L);
    	assertThat(task).isEqualTo(null);
    	
    	// TaskId=99のタスクを検索した時にタスクが存在しないことを確認する（deleted='1'は検索対象外とすること）
    	task = repository.myfindByTaskId(99L);
    	assertThat(task).isEqualTo(null);
    }
    
    
    
    
    
	
}
