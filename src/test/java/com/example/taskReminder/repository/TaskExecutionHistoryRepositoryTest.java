package com.example.taskReminder.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import com.example.taskReminder.entity.TasksExecutionHistory;

@SpringBootTest
@Transactional
@Sql("classpath:sql/taskExecutionHistoryRepositoryTestScheme.sql")
public class TaskExecutionHistoryRepositoryTest {
	
    @Autowired
    private TaskExecutionHistoryRepository repository;
    
    
	@Nested
	class findCreatedAtByTaskIdOrderByCreatedAtDescメソッドの検証 {
    
		@Test
	    void データが存在する時降順で取得できる() {
	    	
	    	List<TasksExecutionHistory> list = repository.findCreatedAtByTaskIdOrderByCreatedAt(11L);
	    	assertThat(list.size()).isEqualTo(3);
	    	
	    	assertThat(list.get(0).getHistoryId()).isEqualTo(1L);
	    	assertThat(list.get(1).getHistoryId()).isEqualTo(2L);
	    	assertThat(list.get(2).getHistoryId()).isEqualTo(3L);
	    	
	    }
		
		@Test
	    void データが存在しない時は取得できない() {
	    	
	    	List<TasksExecutionHistory> list = repository.findCreatedAtByTaskIdOrderByCreatedAt(12L);
	    	assertThat(list.size()).isEqualTo(0);
	    	
	    }
	
	
	
	}
    
    
	

}
