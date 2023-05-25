package io.github.saxomoose.sca.web.controllers;

import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.models.projections.ClosingToCreate;
import io.github.saxomoose.sca.web.models.projections.ClosingToIndex;
import io.github.saxomoose.sca.web.services.ClosingService;
import io.github.saxomoose.sca.web.services.DocumentService;
import io.github.saxomoose.sca.web.services.UserService;
import io.github.saxomoose.sca.web.utils.Notification;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ClosingController {
    private final ClosingService closingService;
    private final DocumentService documentService;
    private final HttpSession httpSession;
    private final UserService userService;

    @Autowired
    public ClosingController(ClosingService closingService, DocumentService documentService, HttpSession httpSession, UserService userService) {
        this.closingService = closingService;
        this.documentService = documentService;
        this.httpSession = httpSession;
        this.userService = userService;
    }

    // Read
    @GetMapping("/closings")
    public ModelAndView index(
        final Principal principal,
        final HttpServletRequest request,
        @SortDefault(sort = "name") Pageable pageable) 
    {
        // final var tenantId = ((Tenant) httpSession.getAttribute("tenant")).id();
        final var tenantId = 1L;
        final ClosingToIndex closingToIndex = closingService.closingsByTenant(tenantId, pageable);
        if (closingToIndex.closingPage().isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        final var modelAndView = new ModelAndView("closings/index");
        modelAndView.addObject("closingToIndex", closingToIndex);
        final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }

    // Create
    @GetMapping("/closings/create")
    public ModelAndView create(
        final HttpServletRequest request,
        @RequestParam(required = false) Integer userPageIndex,
        @RequestParam(required = false) Integer documentPageIndex
    ) 
    {
        if (userPageIndex == null) {
            userPageIndex = 0;
        }
        if (documentPageIndex == null) {
            documentPageIndex = 0;
        }
        final var userPageable = PageRequest.of(userPageIndex, 10);
        final var documentPageable = PageRequest.of(documentPageIndex, 10);
        final var userPage = userService.usersByTenant(1L, userPageable);
        final var documentPage = documentService.documentsByTenant(1L, documentPageable);
        final var modelAndView = new ModelAndView("closings/create");
        modelAndView.addObject("userPage", userPage);
        modelAndView.addObject("documentPage", documentPage);
        final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject(
                "notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }

    /**
     * TODO
     */
    @PostMapping(value = "/closings", consumes = "application/x-www-form-urlencoded")
    public RedirectView store(
        final ClosingToCreate rawClosing,
        final RedirectAttributes redirectAttributes) throws Exception 
    {
        // Validation
        
        final var test = "test";
        final var tenant = (Tenant) httpSession.getAttribute("tenant");
        // final var filename = validationService.documentArgs(rawFilename);
        // signatoryService.create(tenant, filename);
        redirectAttributes.addFlashAttribute(
            "notifications", 
            List.of(new Notification(
                Notification.Type.SUCCESS, 
                "notification.success",
                "users.create.success")));
        return new RedirectView("/users");
    }


}
