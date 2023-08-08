package com.example.taskReminder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.taskReminder.common.MessageAlertLevel;
import com.example.taskReminder.form.UserForm;
import com.example.taskReminder.service.UserService;
import com.example.taskReminder.validation.PasswordEqualsValidator;


@Controller
public class UsersController {
	
	@Autowired
	private UserService userService;
    @Autowired
    PasswordEqualsValidator passwordEqualsValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(passwordEqualsValidator);
    }	
    
	@GetMapping(path = "/users/new")
    public String createForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "users/new";
    }

	@PostMapping(path = "/users/create")
	public String create(
			@Validated UserForm userForm,
			BindingResult result,
			RedirectAttributes redirAttrs,
			Model model) {
		
		if(result.hasErrors()) {
			model.addAttribute("userForm", userForm);
			return "users/new";
		}
		userService.save(userForm);
		
		displayMessageRedirectHelper(
				MessageAlertLevel.SUCCESS,
				"ユーザーを新規作成しました",
				redirAttrs
				);
		
		return "redirect:/login";
	}
	
	private void displayMessageRedirectHelper(
			MessageAlertLevel level,
			String message,
			RedirectAttributes redirAttrs) {
		
		redirAttrs.addFlashAttribute("hasMessage", true);
		redirAttrs.addFlashAttribute("class", level.getCode());
		redirAttrs.addFlashAttribute("message", message);
	
	}
	
}
