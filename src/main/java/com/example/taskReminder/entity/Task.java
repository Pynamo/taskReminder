package com.example.taskReminder.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.example.taskReminder.common.Delete;
import com.example.taskReminder.common.Load;
import com.example.taskReminder.common.Status;

import lombok.Data;

@Entity
@Table(name = "tasks")
@Data
public class Task extends AbstractEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(name = "task_id_seq")
    // 主キーにユニークな値を自動で生成し、@Idを持つフィールドに適用。GenerationType.IDENTITYでテーブルのID列を元に主キー値を生成する
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long taskId;
    
    @Column(nullable = false, name="user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    private Load load;
    
    private String name;
    
    private String content;
    
    @Enumerated(EnumType.STRING)
    private Status status;
    
    //@Enumerated(EnumType.STRING)
    private String deleted;
    
    /*
    @ManyToOne
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;
    */
}