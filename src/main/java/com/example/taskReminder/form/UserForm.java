package com.example.taskReminder.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;

import com.example.taskReminder.validation.constraints.UnusedEmail;

import lombok.Data;

@Data
public class UserForm {

    @NotNull
    @Length(min=1, max=20)
    private String name;
    
    @NotNull
    @Email
    @UnusedEmail
    private String email;
    @NotNull
    @Size(min = 8)
    private String password;

    private String passwordConfirmation;

}
