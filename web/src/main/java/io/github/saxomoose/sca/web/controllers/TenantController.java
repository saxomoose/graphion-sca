package io.github.saxomoose.sca.web.controllers;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;
import io.github.saxomoose.sca.web.config.security.PKIAuthentication;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.models.User;
import io.github.saxomoose.sca.web.services.TenantService;
import io.github.saxomoose.sca.web.services.ValidationService;
import io.github.saxomoose.sca.web.utils.Notification;
import io.github.saxomoose.sca.web.utils.Tuple;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class TenantController {

    private final TenantService tenantService;
    private final ValidationService validationService;

    public TenantController(
        TenantService tenantService,
        ValidationService validationService)
    {
        this.tenantService = tenantService;
        this.validationService = validationService;
    }

    @GetMapping("/tenants/{rawUserUUID}")
    public ModelAndView index(
        @PathVariable final String rawUserUUID,
        @SortDefault(sort = "name") Pageable pageable,
        final HttpServletRequest request) 
    {
        final var userUUID = validationService.uuid(rawUserUUID);
        final var tenantPage = tenantService.tenantsByUser(userUUID, pageable);
        if (tenantPage.isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        final var modelAndView = new ModelAndView("tenants/index");
        modelAndView.addObject("tenantPage", tenantPage);
        final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }

    @PostMapping("/authorization/{rawTenantName}")
    public RedirectView setAuthorization(
        @PathVariable final String rawTenantName,
        @RequestParam final Boolean defaultConnection,
        final RedirectAttributes redirectAttributes,
        final HttpSession httpSession
    )
    {
        // TODO: validate tenant name
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Tuple<Tenant, List<GrantedAuthority>> tenantWithUserAuthority =
            tenantService.tenantWithUserAuthority(user.id(), Boolean.FALSE);
        Authentication authentication = null;
        if (!defaultConnection) {
            authentication = PKIAuthentication.of(
                user,
                tenantWithUserAuthority.second());
        } else if (defaultConnection) {
            authentication = PKIAuthentication.of(
                user,
                tenantWithUserAuthority.first(),
                tenantWithUserAuthority.second());
            tenantService.setDefaultTenant( tenantWithUserAuthority.first().id(), user.id());
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        httpSession.setAttribute("tenant", tenantWithUserAuthority.first());
        
        redirectAttributes.addFlashAttribute(
            "notifications",
            List.of(new Notification(
                Notification.Type.SUCCESS, 
                "authorization.success",
                "authorization.success")));
        return new RedirectView("/closings");
    }
}
