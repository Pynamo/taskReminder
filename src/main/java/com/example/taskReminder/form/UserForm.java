package com.example.taskReminder.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.example.taskReminder.validation.constraints.UnusedEmail;

import lombok.Data;

@Data
public class UserForm {

    @NotEmpty
    @Size(max = 100)
    private String name;
    @NotEmpty
    @Email
    @UnusedEmail
    private String email;
    @NotNull
    @Size(min = 8)
    private String password;

    private String passwordConfirmation;

}
