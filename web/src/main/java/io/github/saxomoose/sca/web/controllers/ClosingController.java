package io.github.saxomoose.sca.web.controllers;

import io.github.saxomoose.sca.web.models.Signatory;
import io.github.saxomoose.sca.web.repositories.ClosingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;

@Controller
public class ClosingController {
    private final ClosingRepository closingRepository;

    @Autowired
    public ClosingController(ClosingRepository closingRepository) {
        this.closingRepository = closingRepository;
    }

    @GetMapping("/closings")
    public ModelAndView index() throws IOException, ClassNotFoundException {
        final var closings = closingRepository.findAll();
        var signatories = new ArrayList<Signatory>();
//        for (final var closing : closings) {
//            signatories.addAll(closing.getSignatories());
//        }

        final var modelAndView = new ModelAndView("closings/index");
        modelAndView.addObject("closings", closings);

        return modelAndView;
    }
}
