package com.saxomoose.eid.web.controllers;

import com.saxomoose.eid.web.dtos.ValidationError;
import com.saxomoose.eid.web.exceptions.UniqueConstraintViolationException;
import com.saxomoose.eid.web.models.User;
import com.saxomoose.eid.web.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.ArrayList;

@Controller
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public ModelAndView register() {
        var modelAndView = new ModelAndView();
        var user = new com.saxomoose.eid.web.dtos.User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("auth/register");

        return modelAndView;
    }

    // Custom registration logic.
    @PostMapping("/register")
    public ModelAndView register(
            @Valid @ModelAttribute("user") final com.saxomoose.eid.web.dtos.User user,
            final BindingResult bindingResult) {
        var modelAndView = new ModelAndView();
        modelAndView.addObject("bindingResult", bindingResult);
        if (!bindingResult.hasErrors()) {
            var userEntity = new User(user.getUsername(), user.getPassword());
            try {
                userService.createNew(userEntity);
            } catch (UniqueConstraintViolationException e) {
                bindingResult.rejectValue("username", "user.username.duplicate");
            }
            if (!bindingResult.hasErrors()) {
                userService.activate(userEntity);
                modelAndView.addObject("message", "Registration successful");
                modelAndView.setViewName("redirect:/");
            }
        }

        if (bindingResult.hasErrors()) {
            var validationErrors = new ArrayList<ValidationError>();
            for (var fieldError : bindingResult.getFieldErrors()) {
                validationErrors.add(new ValidationError(fieldError.getField(), fieldError.getCode()));
            }
            modelAndView.addObject("validationErrors", validationErrors);
            modelAndView.setViewName("auth/register");
        }

        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView login(final Principal principal) {
        var modelAndView = new ModelAndView();
        if (principal != null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        var user = new com.saxomoose.eid.web.dtos.User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("auth/login");

        return modelAndView;
    }
}
