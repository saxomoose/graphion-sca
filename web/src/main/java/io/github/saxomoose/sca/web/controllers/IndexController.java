package io.github.saxomoose.sca.web.controllers;

import java.security.Principal;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @GetMapping("/")
    public ModelAndView index(final Principal principal, final HttpServletRequest request)
    {
        final ModelAndView modelAndView = new ModelAndView();
        final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        if (principal == null) {
            modelAndView.setViewName("index");
        } else {
            modelAndView.setView(new RedirectView("closings"));
        }
        return modelAndView;
    }

    @GetMapping("/pki/sign")
    public ModelAndView sign()
    {
        return new ModelAndView("sign");
    }

    @GetMapping("/ping")
    public ModelAndView ping(HttpServletRequest request)
    {
        final Principal principal = request.getUserPrincipal();
        return new ModelAndView("ping");
    }

    @GetMapping("/pki/ping")
    public ModelAndView pkiPing(HttpServletRequest request)
    {
        final Principal principal = request.getUserPrincipal();
        return new ModelAndView("ping");
    }
}
