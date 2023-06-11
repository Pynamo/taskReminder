package com.example.taskReminder.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import lombok.Data;

@Entity
@Data
public class TasksExecutionHistory extends AbstractEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    @Id
    @SequenceGenerator(name = "history_id_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;
    
    @Column(nullable = false)
    private Long taskId;
    
    private String deleted;

}
