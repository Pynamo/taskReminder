package com.example.taskReminder.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SessionsController {
	
    @GetMapping(path = "/login")
    public String createForm(Model model) {
        return "sessions/new";
    }
    
    @GetMapping(path = "/login-failure")
    public String loginFailure(Model model) {

    	model.addAttribute("hasMessage", true);
    	model.addAttribute("class", "alert-danger");
    	model.addAttribute("message", "ログインに失敗しました");
    	
        return "sessions/new";
    }

}
