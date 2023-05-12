package io.github.saxomoose.sca.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.services.SignatoryService;
import io.github.saxomoose.sca.web.services.TenantService;
import io.github.saxomoose.sca.web.services.ValidationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class SignatoryController {
    private final HttpSession httpSession;
    private final SignatoryService signatoryService;
    private final TenantService tenantService;
    private final ValidationService validationService;

    @Autowired
    public SignatoryController(
        HttpSession httpSession,
        SignatoryService signatoryService,
        TenantService tenantService,
        ValidationService validationService) 
    {
        this.httpSession = httpSession;
        this.signatoryService = signatoryService;
        this.tenantService = tenantService;
        this.validationService = validationService;
    }

    // Read
    @GetMapping("/signatories")
    public ModelAndView index(
        final HttpServletRequest request,
        @SortDefault(sort = "lastName") Pageable pageable) 
    {
        final var tenantId = ((Tenant) httpSession.getAttribute("tenant")).id();
        final var signatoryPage = signatoryService.selectPage(pageable, tenantId);
        if (signatoryPage.isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        final var modelAndView = new ModelAndView("signatories/index");
        modelAndView.addObject("signatoryPage", signatoryPage);
        final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }
    
}
