package io.github.saxomoose.sca.web.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;
import io.github.saxomoose.sca.web.models.Closing;
import io.github.saxomoose.sca.web.models.ClosingSignatory;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.models.User;
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
        // final Long tenantId = ((Tenant) httpSession.getAttribute(Session.TENANT.toString()).id();
        final var tenantId = 1L;
        final ClosingToIndex closingToIndex = closingService.closingsByTenant(tenantId, pageable);
        if (closingToIndex.closingPage().isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        final ModelAndView modelAndView = new ModelAndView("closings/index");
        modelAndView.addObject("closingToIndex", closingToIndex);
        final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }

    // Create
    @GetMapping("/closings/prepare")
    public ModelAndView prepare(
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
        final PageRequest userPageable = PageRequest.of(userPageIndex, 10);
        final PageRequest documentPageable = PageRequest.of(documentPageIndex, 10);
        final Page<User> userPage = userService.usersByTenant(1L, userPageable);
        final Page<Document> documentPage = documentService.documentsByTenant(1L, documentPageable);
        final ModelAndView modelAndView = new ModelAndView("closings/prepare");
        modelAndView.addObject("userPage", userPage);
        modelAndView.addObject("documentPage", documentPage);
        final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }

    /**
     * TODO
     */
    @PostMapping(value = "/closings/prepare", consumes = "application/x-www-form-urlencoded")
    public RedirectView prepare(
        final ClosingToCreate rawClosing,
        final RedirectAttributes redirectAttributes) throws Exception 
    {
        final List<User> users = userService.usersByIds(rawClosing.userIds());
        final List<Document> documents = documentService.documentsByIds(rawClosing.documentIds());
        rawClosing.setUsers(users);
        rawClosing.setDocuments(documents);
        httpSession.setAttribute("closing", rawClosing);
        return new RedirectView("/closings/create");
    }

    @GetMapping("/closings/create")
    public ModelAndView create(
        final HttpServletRequest request
    ) 
    {
        final ClosingToCreate rawClosing = (ClosingToCreate) httpSession.getAttribute("closing");
        final ModelAndView modelAndView = new ModelAndView("closings/create");
        modelAndView.addObject("users", rawClosing.users());
        modelAndView.addObject("documents", rawClosing.documents());
        final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            modelAndView.addObject("notifications", inputFlashMap.get("notifications"));
        }
        return modelAndView;
    }

    @PostMapping(value = "/closings", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView store(
        final HttpServletRequest request,
        @RequestParam final MultiValueMap<String, String> formData,
        final RedirectAttributes redirectAttributes) throws Exception 
    {
        // final Long userId = ((User) ((PkiAuthentication) request.getUserPrincipal()).getPrincipal()).id();
        final Long principalId = 1L;
        // Validation
        List<ClosingSignatory> closingSignatories = new ArrayList<>();
        for (final Map.Entry<String, List<String>> entry : formData.entrySet()) {
            if (!entry.getKey().equals("_csrf")) {
                final Long userId = Long.parseLong(entry.getKey());
                for (final String documentId : entry.getValue()) {
                closingSignatories.add(ClosingSignatory.of(userId, Long.parseLong(documentId)));
                }
            }
        }
        final ClosingToCreate rawClosing = (ClosingToCreate) httpSession.getAttribute("closing");
        // final Long tenantId = ((Tenant) httpSession.getAttribute(SessionEnum.TENANT.toString())).id();
        final Long tenantId = 1L;
        final Closing closing = Closing.of(
            tenantId,
            rawClosing.name(),
            rawClosing.timestamp(),
            rawClosing.zoneId(),
            rawClosing.location(),
            rawClosing.expirationTimestamp()
        );
        closingService.create(closing, principalId, closingSignatories);
        redirectAttributes.addFlashAttribute(
            "notifications", 
            List.of(new Notification(Notification.Type.SUCCESS, "notification.success", "users.create.success")));
        return new RedirectView("/closings");
    }


}
