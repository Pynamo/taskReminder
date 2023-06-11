package com.example.taskReminder.form;

import java.io.Serializable;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class TaskForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long taskId;
 
    private Long userId;
    
    private String load;
    @NotNull
    @Length(min=1, max=20)
    private String name;
    @NotNull
    @Length(min=1, max=100)
    private String content;
    
    private String status;
    
    private UserForm user;

}
