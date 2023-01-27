package com.saxomoose.eid.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

@Controller
public class TestController {
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
