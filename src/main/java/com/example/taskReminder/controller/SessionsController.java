package com.example.taskReminder.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.taskReminder.common.MessageAlertLevel;

@Controller
public class SessionsController {
	
    @GetMapping(path = "/login")
    public String createForm(Model model) {
        return "sessions/new";
    }
    
    @GetMapping(path = "/login-failure")
    public String loginFailure(Model model) {
    
		displayMessageForwardHelper(
				MessageAlertLevel.ERROR, 
				"ログインに失敗しました", 
				model);
    	
        return "sessions/new";
    }
    
	/*
	 * フォワード先にメッセージを表示するためのヘルパー関数
	 */
	private void displayMessageForwardHelper(MessageAlertLevel level, String message, Model model) {

		model.addAttribute("hasMessage", true);
		model.addAttribute("class", level.getCode());
		model.addAttribute("message", message);

	}

}
