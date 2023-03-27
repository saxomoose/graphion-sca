package io.github.saxomoose.sca.web.controllers;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice(basePackages = "io.github.saxomoose.sca.controllers")
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Throwable.class)
    public ModelAndView handle(final Throwable throwable) {
        final var modelAndView = new ModelAndView();
        return modelAndView;
    }

}
