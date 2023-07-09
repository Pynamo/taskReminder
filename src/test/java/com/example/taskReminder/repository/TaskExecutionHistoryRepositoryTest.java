package com.example.taskReminder.repository;

import javax.transaction.Transactional;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Transactional
@Sql("classpath:sql/taskRepositoryTestScheme.sql")
public class TaskExecutionHistoryRepositoryTest {

}
