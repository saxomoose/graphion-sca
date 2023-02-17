package com.saxomoose.eid.web.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.Map;

@Controller
public class IndexController {
    @GetMapping("/")
    public ModelAndView index(HttpServletRequest request) {
        var modelAndView = new ModelAndView();
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }

        modelAndView.setViewName("index");

        return modelAndView;
    }
}