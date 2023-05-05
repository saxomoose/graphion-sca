package io.github.saxomoose.sca.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @GetMapping("/")
    public ModelAndView index(final HttpServletRequest request) 
    {
        // Locale locale = LocaleContextHolder.getLocale();
        // var test = session.getAttribute("session.current.locale");
        // var test2 = session.getAttribute("session.current.timezone");
        final var modelAndView = new ModelAndView();
        final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        logger.info("A info message");
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @GetMapping("/ping")
    public ModelAndView ping(HttpServletRequest request) 
    {
        final var principal = request.getUserPrincipal();
        return new ModelAndView("ping");
    }

    @GetMapping("/pki/ping")
    public ModelAndView pkiPing(HttpServletRequest request) 
    {
        final var principal = request.getUserPrincipal();
        return new ModelAndView("ping");
    }

}
