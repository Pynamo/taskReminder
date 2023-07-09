package com.example.taskReminder.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
public class Task extends AbstractEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public Task(
			Long taskId,
			Long userId,
			String load,
			String name,
			String content,
			String status,
			String imgUrl,
			String deleted) {
		
		this.taskId = taskId;
		this.userId = userId;
		this.load = load;
		this.name = name;
		this.content = content;
		this.status = status;
		this.imgUrl = imgUrl;
		this.deleted = deleted;
	}
	
    
    @Id
    @SequenceGenerator(name = "task_id_seq")
    // 主キーにユニークな値を自動で生成し、@Idを持つフィールドに適用。GenerationType.IDENTITYでテーブルのID列を元に主キー値を生成する
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long taskId;
    
    @Column(nullable = false, name="user_id")
    private Long userId;

    //@Enumerated(EnumType.STRING)
    private String load;
    
    private String name;
    
    private String content;
    
    //@Enumerated(EnumType.STRING)
    private String status;
    
    @Column(name="image_url")
    private String imgUrl;
    
    private String deleted;
    
    
    public void setLoad(Load load) {
    	this.load = load.getCode();
    }
    public Load getLoad() {
    	return Load.getValue(this.load);
    }
    
    public void setStatus(Status status) {
    	this.status = status.getCode();
    }
    public Status getStatus() {
    	return Status.getValue(this.status);
    }
    
    public void setDeleted(Delete deleted) {
    	this.deleted = deleted.getCode();
    }
    public Delete getDeleted() {
    	return Delete.getValue(this.deleted);
    }
    
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

}