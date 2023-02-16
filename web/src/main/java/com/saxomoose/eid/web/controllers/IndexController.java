package com.saxomoose.eid.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {
    @GetMapping("/")
    public ModelAndView index() {
        final var modelAnvView = new ModelAndView();
        modelAnvView.setViewName("index");

        return modelAnvView;
    }
}