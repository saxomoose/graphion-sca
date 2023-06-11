package io.github.saxomoose.sca.web.controllers;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;
import io.github.saxomoose.sca.web.enums.SessionEnum;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.models.User;
import io.github.saxomoose.sca.web.models.projections.UserToCreate;
import io.github.saxomoose.sca.web.services.TenantService;
import io.github.saxomoose.sca.web.services.UserService;
import io.github.saxomoose.sca.web.services.ValidationService;
import io.github.saxomoose.sca.web.utils.Notification;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
    private final HttpSession httpSession;
    private final UserService userService;
    private final TenantService tenantService;
    private final ValidationService validationService;

    @Autowired
    public UserController(
        HttpSession httpSession,
        UserService userService,
        TenantService tenantService,
        ValidationService validationService) 
    {
        this.httpSession = httpSession;
        this.userService = userService;
        this.tenantService = tenantService;
        this.validationService = validationService;
    }

    // Read
    @GetMapping("/users")
    public ModelAndView index(
        final HttpServletRequest request,
        final Pageable pageable) 
    {
        // final Long tenantId = ((Tenant) httpSession.getAttribute(Session.TENANT.toString()).id();
        final var tenantId = 1L;
        final Page<User> userPage = userService.usersByTenant(tenantId, pageable);
        if (userPage.isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        final ModelAndView modelAndView = new ModelAndView("users/index");
        modelAndView.addObject("userPage", userPage);
        final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }

    // Create
    @GetMapping("/users/create")
    public ModelAndView create(final HttpServletRequest request) 
    {
        final ModelAndView modelAndView = new ModelAndView("users/create");
        final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }

    /**
     * TODO
     */
    @PostMapping(
        value = "/users",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView store(
        final UserToCreate rawSignatory,
        final RedirectAttributes redirectAttributes) throws Exception 
    {
        final Tenant tenant = (Tenant) httpSession.getAttribute(SessionEnum.TENANT.toString());
        // final var filename = validationService.documentArgs(rawFilename);
        // signatoryService.create(tenant, filename);
        redirectAttributes.addFlashAttribute(
            "notifications", 
            List.of(new Notification(Notification.Type.SUCCESS, "notification.success", "users.create.success")));
        return new RedirectView("/users");
    }
    
}
