package io.github.saxomoose.sca.web.controllers;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import io.github.saxomoose.sca.web.dtos.User;
import io.github.saxomoose.sca.web.services.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class JDBCAuthController {
    private static final Logger logger = LoggerFactory.getLogger(JDBCAuthController.class);

    private final UserService userService;

    @Autowired
    public JDBCAuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public ModelAndView register(
        final HttpServletRequest request,
        final Principal principal) 
    {
        final var modelAndView = new ModelAndView();
        if (principal == null) {
            modelAndView.setViewName("auth/register");
            final var userDTO = new User();
            modelAndView.addObject("user", userDTO);

            final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
            if (inputFlashMap != null) {
                modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
            }
        } else {
            // return if user is already logged in.
            modelAndView.setViewName("redirect:/");
        }
        return modelAndView;
    }

    // @PostMapping(value = "/register")
    // public RedirectView register(
    // @ModelAttribute final User userDTO,
    // final BindingResult bindingResult,
    // final RedirectAttributes redirectAttributes) {
    // final var redirectView = new RedirectView();
    // if (!bindingResult.hasErrors()) {
    // try {
    // userService.create(userDTO);
    // } catch (DatabaseConstraintViolationException exception) {
    // bindingResult.rejectValue("username", "duplicate");
    // }
    // if (!bindingResult.hasErrors()) {
    // redirectView.setUrl("/");
    // redirectAttributes.addFlashAttribute("notifications", List.of(new
    // Notification(Notification.Type.SUCCESS, "notification.success",
    // "register.success")));
    // }
    // }
    // if (bindingResult.hasErrors()) {
    // redirectView.setUrl("/register");
    // final var validationErrors = new ArrayList<Notification>();
    // for (final var fieldError : bindingResult.getFieldErrors()) {
    // //noinspection DataFlowIssue
    // validationErrors.add(new Notification(Notification.Type.ERROR,
    // "notification.error.validation", fieldError.getCodes()[0]));
    // }
    // redirectAttributes.addFlashAttribute("notifications", validationErrors);
    // }
    // return redirectView;
    // }

    @GetMapping("/login")
    public ModelAndView login(
        final Principal principal,
        final HttpServletRequest request) 
    {
        final var modelAndView = new ModelAndView();
        if (principal == null) {
            modelAndView.setViewName("auth/login");
            // On failed login.
            final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
            if (inputFlashMap != null) {
                modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
            }
        } else {
            // return if user is already logged in.
            modelAndView.setView(new RedirectView("/"));
        }
        return modelAndView;
    }
}
