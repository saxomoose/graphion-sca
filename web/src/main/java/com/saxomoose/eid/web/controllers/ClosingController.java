package com.saxomoose.eid.web.controllers;

import com.saxomoose.eid.web.repositories.ClosingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
public class ClosingController {
    private final ClosingRepository closingRepository;

    @Autowired
    public ClosingController(ClosingRepository closingRepository) {
        this.closingRepository = closingRepository;
    }

    @GetMapping("/closings")
    public ModelAndView index() throws IOException, ClassNotFoundException {
        var closings = closingRepository.findAll();

        var modelAndView = new ModelAndView("closings/index");
        modelAndView.addObject("closings", closings);

        return modelAndView;
    }
}
