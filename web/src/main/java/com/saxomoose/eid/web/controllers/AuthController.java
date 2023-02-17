package com.saxomoose.eid.web.controllers;

import com.saxomoose.eid.web.dtos.Notification;
import com.saxomoose.eid.web.enums.NotificationType;
import com.saxomoose.eid.web.exceptions.UniqueConstraintViolationException;
import com.saxomoose.eid.web.models.User;
import com.saxomoose.eid.web.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public ModelAndView register(HttpServletRequest request, Principal principal) {
        // Early return if user is already logged in.
        var modelAndView = new ModelAndView();
        if (principal != null) {
            modelAndView.setViewName("redirect:/");

            return modelAndView;
        }

        var user = new com.saxomoose.eid.web.dtos.User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("auth/register");

        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }

        return modelAndView;
    }

    // Custom registration logic.
    @PostMapping(value = "/register")
    public RedirectView register(
            @Valid
            @ModelAttribute final com.saxomoose.eid.web.dtos.User user,
            final BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        var redirectView = new RedirectView();
        if (!bindingResult.hasErrors()) {
            var userEntity = new User(user.getUsername(), user.getPassword());
            try {
                userService.createNew(userEntity);
            } catch (UniqueConstraintViolationException e) {
                bindingResult.rejectValue("username", "duplicate");
            }
            if (!bindingResult.hasErrors()) {
                userService.activate(userEntity);
                redirectAttributes.addFlashAttribute("notifications", List.of(new Notification(NotificationType.SUCCESS, "notification.success", "register.success")));
                redirectView.setUrl("/");
            }
        }

        if (bindingResult.hasErrors()) {
            var validationErrors = new ArrayList<Notification>();
            for (var fieldError : bindingResult.getFieldErrors()) {
                //noinspection DataFlowIssue
                validationErrors.add(new Notification(NotificationType.ERROR, "notification.error.validation", fieldError.getCodes()[0]));
            }
            redirectAttributes.addFlashAttribute("notifications", validationErrors);
            redirectView.setUrl("/register");
        }

        return redirectView;
    }

    @GetMapping("/login")
    public ModelAndView login(
            Principal principal,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        var modelAndView = new ModelAndView();
        // Early return if user is already logged in.
        if (principal != null) {
            modelAndView.setViewName("redirect:/");

            return modelAndView;
        }

        var user = new com.saxomoose.eid.web.dtos.User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("auth/login");

        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }


        return modelAndView;
    }
}
