package io.github.saxomoose.sca.web.controllers;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import io.github.saxomoose.sca.web.models.Closing;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.services.ClosingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ClosingController {
    private final ClosingService closingService;
    private final HttpSession httpSession;

    @Autowired
    public ClosingController(ClosingService closingService, HttpSession httpSession) {
        this.closingService = closingService;
        this.httpSession = httpSession;
    }

    // Read
    @GetMapping("/closings")
    public ModelAndView index(
        final Principal principal,
        final HttpServletRequest request,
        @SortDefault(sort = "name") Pageable pageable) 
    {
        final var tenantId = ((Tenant) httpSession.getAttribute("tenant")).id();
        final Page<Closing> closingPage = closingService.selectPage(pageable, tenantId);
        if (closingPage.isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        final var modelAndView = new ModelAndView("closings/index");
        modelAndView.addObject("closingPage", closingPage);
        final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }

        // Create
        @GetMapping("/closings/create")
        public ModelAndView create(final HttpServletRequest request) 
        {
            final var modelAndView = new ModelAndView("closings/create");
            final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
            if (inputFlashMap != null) {
                modelAndView.addObject(
                    "notifications", inputFlashMap.get("notifications"));
            }
            return modelAndView;
        }
}
