package io.github.saxomoose.sca.web.controllers;

import io.github.saxomoose.sca.web.repositories.ClosingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ClosingController {
    private final ClosingRepository closingRepository;

    @Autowired
    public ClosingController(ClosingRepository closingRepository) {
        this.closingRepository = closingRepository;
    }

    @GetMapping("/closings")
    public ModelAndView index() {
//        final var closings = closingRepository.findAll();

        final var modelAndView = new ModelAndView("closings/index");
//        modelAndView.addObject("closings", closings);

        return modelAndView;
    }
}
