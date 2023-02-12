package com.saxomoose.eid.web.controllers;

import com.saxomoose.eid.web.models.ApplicationUser;
import com.saxomoose.eid.web.services.ApplicationUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class AuthController {
    private final ApplicationUserService applicationUserService;

    @Autowired
    public AuthController(ApplicationUserService applicationUserService)
    {
        this.applicationUserService  = applicationUserService;
    }

    @GetMapping("/login")
    public String login(Principal principal) {
        if (principal != null) {
            return "redirect:/";
        }

        return "auth/login";
    }

    @GetMapping("/register")
    public ModelAndView register() {
        var modelAndView = new ModelAndView();
        var user = new ApplicationUser();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("auth/register");

        return modelAndView;
    }

    // Custom registration logic.
    @PostMapping("/register")
    public ModelAndView register(@Valid @ModelAttribute("user") ApplicationUser user, BindingResult bindingResult)
    {
        if (applicationUserService.findByUsername(user.getUsername()) != null) {
            bindingResult.rejectValue("username", "error.user", "A user with that email address already exists");
        }
        var modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("BindingResult", bindingResult);
            modelAndView.setViewName("auth/register");
        } else {
            applicationUserService.createNew(user);
            applicationUserService.activate(user);
            modelAndView.addObject("message", "Registration successful");
            modelAndView.setViewName("redirect:/");
        }

        return modelAndView;
    }
}
