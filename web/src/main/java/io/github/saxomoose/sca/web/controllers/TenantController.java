package io.github.saxomoose.sca.web.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import io.github.saxomoose.sca.web.services.TenantService;
import io.github.saxomoose.sca.web.services.ValidationService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class TenantController {

    private final TenantService tenantService;
    private final ValidationService validationService;

    public TenantController(TenantService tenantService, ValidationService validationService)
    {
        this.tenantService = tenantService;
        this.validationService = validationService;
    }

    @GetMapping("/tenants/{rawUserUUID}")
    public ModelAndView index(
        final HttpServletRequest request,
        final String rawUserUUID,
        @SortDefault(sort = "name") Pageable pageable) 
    {
        final var userUUID = validationService.uuid(rawUserUUID);
        final var tenantPage = tenantService.tenantsByUser(userUUID, pageable);
        if (tenantPage.isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        final var modelAndView = new ModelAndView("users/index");
        modelAndView.addObject("tenantPage", tenantPage);
        final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }
}
